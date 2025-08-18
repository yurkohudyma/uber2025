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
import ua.hudyma.rideservice.dto.RideRequestDto;
import ua.hudyma.rideservice.dto.RouteDistanceResponseDto;
import ua.hudyma.rideservice.dto.RouteDto;
import ua.hudyma.rideservice.dto.RoutePoint;
import ua.hudyma.rideservice.enums.TrackDirection;
import ua.hudyma.rideservice.exception.RideAllreadyAcceptedException;
import ua.hudyma.rideservice.exception.RideNotAcceptedException;
import ua.hudyma.rideservice.repository.RideRepository;
import ua.hudyma.rideservice.repository.VehicleRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static ua.hudyma.rideservice.enums.RideStatus.*;

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

    private final RideRepository rideRepository;
    private final VehicleRepository vehicleRepository;
    private final KaffkaProducer kaffkaProducer;
    private final UserClient userClient;
    private final GeoClient geoClient;

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

    public boolean declineAcceptedRideByDriver (RideRequestDto rideRequestDto){
        var ride = rideRepository
                .findById(rideRequestDto.rideId())
                .orElseThrow(
                        () ->
                                new IllegalArgumentException("Ride has NOT BEEN FOUND"));
        if (!isRideAcceptedByTheDriver(ride.getDriverId(), rideRequestDto.driverId())){
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
        return true;
    }

    private boolean isRideAcceptedByTheDriver(
            String actualDriverId, String requestDriverId) {
        if (actualDriverId != null && requestDriverId != null) {
            return actualDriverId.equals(requestDriverId);
        }
        return false;
    }

    @Transactional
    public boolean acceptRideByDriver(RideRequestDto rideRequestDto) {
        if (isRideAccepted(rideRequestDto)){
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
            log.warn(" --> current pos of vehicle {} is NA, setting default", vehicleId);
        }
        var toPaxRouteDto = new RouteDto(
                vehicleCurPos,
                ride.getDestination(),
                TrackDirection.toPAX);
        ride.setRouteList(responseDtoTrack.routePoints());

        var toPaxRouteResponseDto = geoClient
                .getDistance(toPaxRouteDto, true);
        var toPaxRouteDistance = toPaxRouteResponseDto.distance();
        ride.setToPaxRouteDistance(BigDecimal.valueOf(toPaxRouteDistance));
        ride.setToPaxRouteList(toPaxRouteResponseDto.routePoints());
        //todo engageVehicleMovementOnTrack();
        dispatchVehicleToDeparturePoint(vehicle, ride);
        ride.setRideStatus(IN_PROGRESS);
        rideRepository.save(ride);
        return true;
    }

    private boolean isRideAccepted(RideRequestDto rideRequestDto) {
        var rideStatus = rideRepository
                .findById(rideRequestDto.rideId()).orElseThrow(
                () -> new IllegalArgumentException("Ride has NOT BEEN FOUND"))
                .getRideStatus();
        return Objects.nonNull(rideRequestDto.vehicleId()) &&
               Objects.nonNull(rideRequestDto.driverId()) &&
                rideStatus != REQUESTED && rideStatus != DECLINED_BY_DRIVER;
    }

    public void dispatchVehicleToDeparturePoint(Vehicle vehicle, Ride ride) {
        var toPaxRouteDistance = ride.getToPaxRouteDistance();
        log.info(" --> toPax Distance is {} km", toPaxRouteDistance);
        var timeToDestination = toPaxRouteDistance
                .divide(baseSpeed, 9, RoundingMode.HALF_UP);
        log.info(" --> Time to DEST = {} hrs", timeToDestination);
        var toPaxRouteList = ride.getToPaxRouteList();
        if (toPaxRouteList.isEmpty()){
            throw new NoSuchElementException("toPaxRouteList is EMPTY");
        }
        var time = convertToLocalTime(timeToDestination);
        log.info(" --> Ride duration = {} min", time);
        var intervalSeconds = timeToDestination.doubleValue() * 3600 / toPaxRouteList.size();
        log.info(" --> GPS update interval = {} sec", intervalSeconds);

        var scheduler = Executors.newSingleThreadScheduledExecutor();
        var route = toPaxRouteList
                .stream()
                .map(coords -> new RoutePoint(coords[0], coords[1]))
                .toList();
        var index = new AtomicInteger();

        Runnable task = () -> {
            int i = index.getAndIncrement();
            if (i < route.size()) {
                vehicle.setCurrentPosition(route.get(i));
                log.info("Moved to point {} out of {} ({})",
                        i,
                        route.size(),
                        route.get(i));
            } else {
                scheduler.shutdown();
                log.info("Vehicle reached DEPART. Shutting down scheduler");
            }
        };
        scheduler.scheduleAtFixedRate(
                task, 0,
                Math.round(intervalSeconds),
                TimeUnit.SECONDS);
        vehicleRepository.save(vehicle);
        log.info("Vehicle {} position has moved to {}",
                vehicle.getId(), vehicle.getCurrentPosition());
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
}