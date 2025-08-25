package ua.hudyma.rideservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ua.hudyma.rideservice.client.GeoClient;
import ua.hudyma.rideservice.client.UserClient;
import ua.hudyma.rideservice.domain.Ride;
import ua.hudyma.rideservice.domain.Vehicle;
import ua.hudyma.rideservice.dto.*;
import ua.hudyma.rideservice.constants.RideStatus;
import ua.hudyma.rideservice.constants.TrackDirection;
import ua.hudyma.rideservice.exception.RideAllreadyAcceptedException;
import ua.hudyma.rideservice.exception.RideNotAcceptedException;
import ua.hudyma.rideservice.exception.VehicleNotAssignedToRideException;
import ua.hudyma.rideservice.repository.RideRepository;
import ua.hudyma.rideservice.repository.VehicleRepository;
import ua.hudyma.rideservice.util.DistanceCalculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;
import static ua.hudyma.rideservice.constants.RideStatus.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class RideService {
    @Value("${kafka.topic-name}")
    private String topic;
    @Value("${uber2025.base-price}")
    private BigDecimal basePrice;
    @Value("${uber2025.vehicle.base-speed}")
    private BigDecimal baseSpeed;
    @Value("${uber2025.vehicle.default-position.latitude}")
    private BigDecimal defaultLatitude;
    @Value("${uber2025.vehicle.default-position.longitude}")
    private BigDecimal defaultLongitude;
    @Value("${uber2025.cities.ivano-frankivsk.centre.latitude}")
    private Double centreLatitude;
    @Value("${uber2025.cities.ivano-frankivsk.centre.longitude}")
    private Double centreLongitude;
    @Value("${uber2025.cities.ivano-frankivsk.city-square}")
    private double citySquare;

    private final RideRepository rideRepository;
    private final VehicleRepository vehicleRepository;
    private final KaffkaProducer kaffkaProducer;
    private final UserClient userClient;
    private final GeoClient geoClient;
    private final RideStatusService rideStatusService;

    @Transactional
    public Ride addRide(RideRequestDto requestDto) {
        var paxId = requestDto.paxId();
        var departure = requestDto.routeDto().departure();
        var destination = requestDto.routeDto().destination();
        var ride = new Ride();
        ride.setDeparture(departure);
        ride.setDestination(destination);
        ride.setRideStatus(REQUESTED);
        if (!userClient.paxExists(paxId)) {
            var msg = format("User with paxId = %s has NOT BEEN FOUND", paxId);
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        ride.setPaxId(paxId);
        var vehiclePriceCoeff = requestDto
                .vehicleClass()
                .getPriceCoefficient();
        var distance = BigDecimal.valueOf(getDistance(requestDto.routeDto(), false).distance());
        ride.setRouteDistance(distance);
        ride.setRidePrice(distance
                .multiply(vehiclePriceCoeff)
                .multiply(basePrice));
        rideRepository.save(ride);
        log.info("ride {} has been saved", ride.getId());
        var msg = format("ride %s requested by pax %s", ride.getId(), paxId);
        kaffkaProducer.sendMessage(topic, msg);
        return ride;
    }

    public RideStatus declineAcceptedRideByDriver(RideRequestDto rideRequestDto) {
        var ride = rideRepository
                .findById(rideRequestDto.rideId())
                .orElseThrow(
                        () ->
                                new IllegalArgumentException("Ride has NOT BEEN FOUND"));
        if (isRideAcceptedByTheDriver(ride.getDriverId(), rideRequestDto.driverId())) {
            throw new RideNotAcceptedException("ride HAS not been accepted by this Driver");
        }
        ride.setRideStatus(DECLINED_BY_DRIVER);
        ride.setDriverId(null);
        ride.setVehicle(null);
        var vehicle = vehicleRepository
                .findById(rideRequestDto.vehicleId())
                .orElseThrow(
                        () ->
                                new IllegalArgumentException("Vehicle has NOT BEEN FOUND"));
        vehicle.getRideList().remove(ride);
        rideRepository.save(ride);
        vehicleRepository.save(vehicle);
        return DECLINED_BY_DRIVER;
    }

    private boolean isRideAcceptedByTheDriver(
            String actualDriverId, String requestDriverId) {
        if (actualDriverId != null && requestDriverId != null) {
            return !actualDriverId.equals(requestDriverId);
        }
        return false;
    }

    @Transactional
    public RideStatus acceptRideByDriver(RideRequestDto rideRequestDto) {
        if (isRideAccepted(rideRequestDto)) {
            throw new RideAllreadyAcceptedException("ride HAS already been accepted");
        }
        var driverId = rideRequestDto.driverId();
        if (!userClient.driverExists(driverId)) {
            var msg = format("User with driverId = %s has NOT BEEN FOUND", driverId);
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        var vehicleId = rideRequestDto.vehicleId();
        var vehicle = vehicleRepository.findById(vehicleId).orElseThrow(
                () -> new IllegalArgumentException("Vehicle has NOT BEEN FOUND"));
        var ride = rideRepository
                .findById(rideRequestDto.rideId())
                .orElseThrow(
                        () ->
                                new IllegalArgumentException("Ride has NOT BEEN FOUND"));

        ride.setVehicle(vehicle);
        ride.setDriverId(driverId);
        vehicle.getRideList().add(ride);
        var routeDto = new RouteDto(ride.getDeparture(),
                ride.getDestination(),
                TrackDirection.ROUTE);
        var responseDtoTrack = geoClient
                .getDistance(routeDto, false);
        var vehicleCurPos = vehicle.getCurrentPosition();
        if (vehicleCurPos.latitude() == 0 ||
                vehicleCurPos.longitude() == 0) {
            vehicle.setCurrentPosition(
                    new RoutePoint(
                            defaultLatitude.doubleValue(),
                            defaultLongitude.doubleValue()));
            log.warn(" --> current pos of vehicle {} " +
                    "is NA, setting default", vehicleId);
        }
        var toPaxRouteDto = new RouteDto(
                vehicleCurPos,
                ride.getDeparture(),
                TrackDirection.toPAX);
        ride.setRouteList(responseDtoTrack.routePoints());

        var toPaxRouteResponseDto = geoClient
                .getDistance(toPaxRouteDto, true);
        var toPaxRouteDistance = toPaxRouteResponseDto.distance();
        ride.setToPaxRouteDistance(BigDecimal.valueOf(toPaxRouteDistance));
        ride.setToPaxRouteList(toPaxRouteResponseDto.routePoints());
        var success = dispatchVehicleToDeparturePoint(vehicle, ride);
        ride.setRideStatus(IN_PROGRESS);
        //rideRepository.save(ride);
        return success ? IN_PROGRESS : EXPIRED;
    }

    private boolean isRideAccepted(RideRequestDto rideRequestDto) {
        var rideStatus = rideRepository
                .findById(rideRequestDto.rideId()).orElseThrow(
                        () -> new IllegalArgumentException
                                ("Ride has NOT BEEN FOUND"))
                .getRideStatus();
        return Objects.nonNull(rideRequestDto.vehicleId()) &&
                Objects.nonNull(rideRequestDto.driverId()) &&
                rideStatus != REQUESTED && rideStatus != DECLINED_BY_DRIVER;
    }

    public boolean dispatchVehicleToDeparturePoint(Vehicle vehicle, Ride ride) {
        var toPaxRouteDistance = ride.getToPaxRouteDistance();
        log.info(" --> toPax Distance is {} km", toPaxRouteDistance);
        var timeToPickupPax = toPaxRouteDistance
                .divide(baseSpeed, 9, RoundingMode.HALF_UP);
        log.info(" --> Time to PICKUP = {} hrs", timeToPickupPax);
        log.info(" --> constant velocity = {} km/h", baseSpeed);
        var toPaxRouteList = ride.getToPaxRouteList();
        if (toPaxRouteList.isEmpty()) {
            throw new NoSuchElementException("toPaxRouteList is EMPTY");
        }
        var time = convertToLocalTime(timeToPickupPax);
        log.info(" --> Ride duration = {} min", time);
        var intervalSeconds = timeToPickupPax.doubleValue() * 3600 /
                toPaxRouteList.size();
        log.info(" --> GPS update interval = {} sec",
                intervalSeconds);

        var scheduler = Executors
                .newSingleThreadScheduledExecutor();
        var route = convertListOfDoubleArraysIntoListOfRoutePoints(
                toPaxRouteList);
        var index = new AtomicInteger();

        Runnable task = () -> runExecutor(vehicle, index,
                route, scheduler, "DEPART", ride);
        var interval = Math.round(intervalSeconds);
        interval = interval <= 0 ? 1 : interval;
        scheduler.scheduleAtFixedRate(
                task, 0,
                interval,
                TimeUnit.SECONDS);
        return true;
    }

    private void runExecutor(Vehicle vehicle, AtomicInteger index,
                             List<RoutePoint> route,
                             ScheduledExecutorService scheduler,
                             String destIdentifier, Ride ride) {
        int i = index.getAndIncrement();
        if (i < route.size()) {
            vehicle.setCurrentPosition(route.get(i));
            vehicleRepository.save(vehicle);
            log.info("Moved to point {} out of {} ({})",
                    i,
                    route.size(),
                    route.get(i));
        } else {
            scheduler.shutdown();
            log.info("Vehicle reached " + destIdentifier + ". Shutting down scheduler");
            log.info("Vehicle {} position: [{}, {}]",
                    vehicle.getVehicleRegistrationNumber(), vehicle
                            .getCurrentPosition().latitude(),
                    vehicle.getCurrentPosition().longitude());
            if (destIdentifier.equals("DESTINATION") && getDistanceHaversine(new RouteDto(
                    vehicle.getCurrentPosition(), route.get(route.size() - 1), null)) <= 0.01){
                ride.setRideStatus(COMPLETE);
                rideRepository.save(ride);
            }
        }
    }

    @Transactional
    public boolean initTransfer(RideRequestDto rideRequestDto) {
        var rideId = rideRequestDto.rideId();
        var ride = rideRepository
                .findById(rideId)
                .orElseThrow(
                        () ->
                                new IllegalArgumentException("Ride has NOT BEEN FOUND"));
        if (isRideAcceptedByTheDriver(ride.getDriverId(), rideRequestDto.driverId())) {
            throw new RideNotAcceptedException("ride HAS not been accepted by this Driver");
        }
        if (ride.getRideStatus() != IN_PROGRESS) {
            log.error("ride doesn't seem to be IN_PROGRESS status " +
                    "which could mean no DRIVER has accepted the ride");
            throw new RideNotAcceptedException("ride HAS not been accepted by this Driver, " +
                    "ride status is INVALID");
        }
        rideStatusService.setRideStatusOnBoardAndFlush(rideId);
        ride.setRideStatus(PAX_ONBOARD);
        var routeDistance = ride.getRouteDistance();
        RouteDistanceResponseDto responseDto = null;
        if (routeDistance == null) {
            var routeDto = new RouteDto(
                    ride.getDeparture(),
                    ride.getDestination(),
                    null);
            responseDto = getDistance(
                    routeDto, true);
        }
        if (responseDto == null) {
            throw new IllegalArgumentException("Fatal error fetching DATA FROM GRAPHHOPPER");
        }
        routeDistance = BigDecimal.valueOf(responseDto.distance());
        log.info(" --> Distance to Destination is {} km", routeDistance);
        ride.setRouteDistance(routeDistance);

        var routeList = ride.getRouteList();
        if (routeList == null || routeList.isEmpty()) {
            routeList = responseDto.routePoints();
            if (routeList == null || routeList.isEmpty()) {
                throw new NoSuchElementException("routeList is NULL or EMPTY");
            }
        }
        var timeToDestination = routeDistance.divide(
                baseSpeed, 9, RoundingMode.HALF_UP);
        log.info(" --> Time to PICKUP = {} hrs", timeToDestination);
        var time = convertToLocalTime(timeToDestination);
        log.info(" --> Transfer duration = {} min", time);
        log.info(" --> constant velocity = {} km/h", baseSpeed);
        var intervalSeconds = timeToDestination.doubleValue() * 3600 /
                routeList.size();
        log.info(" --> GPS update interval = {} sec",
                intervalSeconds);
        var route = convertListOfDoubleArraysIntoListOfRoutePoints(routeList);
        var index = new AtomicInteger();
        var vehicle = ride.getVehicle();
        if (vehicle == null) {
            throw new VehicleNotAssignedToRideException(
                    "Attempt to process ride with no vehicle ASSIGNED");
        }
        var scheduler = Executors
                .newSingleThreadScheduledExecutor();
        Runnable task = () -> runExecutor(vehicle, index,
                route, scheduler, "DESTINATION", null);
        var interval = Math.round(intervalSeconds);
        interval = interval <= 0 ? 1 : interval;
        scheduler.scheduleAtFixedRate(
                task, 0,
                interval,
                TimeUnit.SECONDS);
        return true;
    }

    private static List<RoutePoint> convertListOfDoubleArraysIntoListOfRoutePoints(
            List<double[]> toPaxRouteList) {
        return toPaxRouteList
                .stream()
                .map(coords -> new RoutePoint(
                        coords[0], coords[1]))
                .toList();
    }

    private LocalTime convertToLocalTime(BigDecimal timeToDestination) {
        int hours = timeToDestination.intValue();
        int minutes = timeToDestination
                .remainder(BigDecimal.ONE)
                .multiply(BigDecimal.valueOf(60))
                .intValue();
        return LocalTime.of(hours, minutes);
    }

    public boolean existsByPaxId(String paxId) {
        var msg = format("pax %s has been requested", paxId);
        kaffkaProducer.sendMessage(topic, msg);
        return userClient.paxExists(paxId);
    }

    public boolean existsByDriverId(String driverId) {
        var msg = format("driver %s has been requested", driverId);
        kaffkaProducer.sendMessage(topic, msg);
        return userClient.driverExists(driverId);
    }

    public RouteDistanceResponseDto getDistance(RouteDto dto, boolean withTrack) {
        return geoClient.getDistance(dto, withTrack);
    }

    public double getDistanceHaversine(RouteDto dto) {
        return DistanceCalculator.haversine(dto);
    }

    public List<DistanceResponseDto> getAllVehiclesWithinCityRadius() {
        double defCityRadius = Math.sqrt(citySquare / Math.PI);
        return vehicleRepository
                .findAll()
                .stream()
                .map(vehicle -> {
                    double distance = getDistance(
                            new RouteDto(
                                    new RoutePoint(vehicle.getCurrentPosition().latitude(),
                                            vehicle.getCurrentPosition().longitude()),
                                    new RoutePoint(centreLatitude, centreLongitude),
                                    null
                            ),
                            false
                    ).distance();
                    return new AbstractMap.SimpleEntry<>(vehicle, distance);
                })
                .filter(entry -> entry.getValue() <= defCityRadius)
                .map(entry -> new DistanceResponseDto( entry.getValue(), entry.getKey().getId()))
                .toList();
    }

    public Optional<DistanceResponseDto> getNearestVehicleToDefCentre() {
        return vehicleRepository
                .findAll()
                .stream()
                .map(vehicle -> {
                    Double distance = getDistanceHaversine(new RouteDto(
                            new RoutePoint(vehicle.getCurrentPosition().latitude(),
                                    vehicle.getCurrentPosition().longitude()),
                            new RoutePoint(centreLatitude, centreLongitude),
                            null
                    ));
                    return new AbstractMap.SimpleEntry<>(distance, vehicle.getId());
                })
                .min(Map.Entry.comparingByValue())
                .map(entry -> new DistanceResponseDto(
                        entry.getKey(),
                        entry.getValue()));
    }
}