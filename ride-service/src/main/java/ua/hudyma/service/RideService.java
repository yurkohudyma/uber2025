package ua.hudyma.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.client.GeoClient;
import ua.hudyma.client.UserClient;
import ua.hudyma.domain.Ride;
import ua.hudyma.domain.Vehicle;
import ua.hudyma.dto.RideRequestDto;
import ua.hudyma.dto.RouteDistanceResponseDto;
import ua.hudyma.dto.RouteDto;
import ua.hudyma.dto.RoutePoint;
import ua.hudyma.enums.RideStatus;
import ua.hudyma.repository.RideRepository;
import ua.hudyma.repository.VehicleRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static ua.hudyma.enums.RideStatus.IN_PROGRESS;

@Service
@RequiredArgsConstructor
@Log4j2
public class RideService {
    @Value("${kafka.topic-name}")
    private String topic;
    @Value("${uber2025.base-price}")
    private BigDecimal basePrice;
    @Value("${uber2025.base-speed}")
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
        ride.setRideStatus(RideStatus.REQUESTED);
        if (!userClient.paxExists(paxId)) {
            var msg = format("User with paxId = %s has NOT BEEN FOUND", paxId);
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        ride.setPaxId(paxId);
        var vehiclePriceCoeff = requestDto
                .vehicleClass()
                .getPriceCoefficient();
        var distance = BigDecimal.valueOf(getDistance(requestDto.routeDto()).distance());
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

    @Transactional
    public boolean acceptRideByDriver(RideRequestDto rideRequestDto) {
        var driverId = rideRequestDto.driverId();
        if (!userClient.driverExists(driverId)) {
            var msg = format("User with driverId = %s has NOT BEEN FOUND", driverId);
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        var vehicleId = rideRequestDto.vehicleId();
        var vehicle = vehicleRepository.findById(vehicleId).orElseThrow(
                () -> new IllegalArgumentException("Vehicle has NOT BEEN FOUND"));
        var ride = rideRepository.findById(rideRequestDto.rideId()).orElseThrow(
                () -> new IllegalArgumentException("Ride has NOT BEEN FOUND"));

        ride.setVehicle(vehicle);
        ride.setDriverId(driverId);
        vehicle.getRideList().add(ride);
        var routeDto = new RouteDto(ride.getDeparture(), ride.getDestination());
        var responseDtoTrack = geoClient
                .getDistanceWithTrack(routeDto);
        var vehicleCurPos = vehicle.getCurrentPosition();
        if (vehicleCurPos.latitude() == 0 ||
                vehicleCurPos.longitude() == 0) {
            vehicle.setCurrentPosition(
                    new RoutePoint(
                            defaultLatitude.doubleValue(),
                            defaultLongitude.doubleValue()));
            log.warn("current pos of vehicle {} is NA, setting default", vehicleId);
        }
        var toPaxRouteDto = new RouteDto(
                vehicleCurPos,
                routeDto.departure());
        ride.setRouteList(responseDtoTrack.routePoints());

        var toPaxRouteResponseDto = geoClient
                .getDistanceWithTrack(toPaxRouteDto);
        var toPaxRouteDistance = toPaxRouteResponseDto.distance();
        ride.setToPaxRouteDistance(BigDecimal.valueOf(toPaxRouteDistance));
        ride.setToPaxRouteList(toPaxRouteResponseDto.routePoints());
        //todo engageVehicleMovementOnTrack();
        dispatchVehicleToDeparturePoint(vehicle, ride);
        ride.setRideStatus(IN_PROGRESS);
        return true;
    }

    public void dispatchVehicleToDeparturePoint(Vehicle vehicle, Ride ride) {
        var toPaxRouteDistance = ride.getToPaxRouteDistance();
        var timeToDestination = toPaxRouteDistance
                .divide(baseSpeed, 9, RoundingMode.HALF_UP);
        var toPaxRouteList = ride.getToPaxRouteList();
        if (toPaxRouteList.isEmpty()){
            throw new NoSuchElementException("toPaxRouteList is EMPTY");
        }
        var time = convertToLocalTime(timeToDestination);
        log.info("ETA = T + {}", time);
        var intervalSeconds = timeToDestination.doubleValue() * 3600 / toPaxRouteList.size();
        log.info("-> interval of acquiring GPS data = {} sec", intervalSeconds);

        try (var scheduler = newSingleThreadScheduledExecutor()) {
            var route = toPaxRouteList
                    .stream()
                    .map(coords ->
                            new RoutePoint(coords[0], coords[1]))
                    .toList();
            var index = new AtomicInteger();

            Runnable task = () -> {
                int i = index.getAndIncrement();
                if (i < route.size()) {
                    vehicle.setCurrentPosition(route.get(i));
                    vehicleRepository.save(vehicle);
                    log.info("Moved to point {}", i);
                } else {
                    scheduler.shutdown();
                }
            };

            scheduler.scheduleAtFixedRate(
                    task, 0,
                    Math.round(intervalSeconds),
                    TimeUnit.SECONDS);
        }
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

    public RouteDistanceResponseDto getDistance(RouteDto dto) {
        return geoClient.getDistance(dto);
    }

    public RouteDistanceResponseDto getDistanceWithTrack(RouteDto dto) {
        return geoClient.getDistanceWithTrack(dto);
    }
}
