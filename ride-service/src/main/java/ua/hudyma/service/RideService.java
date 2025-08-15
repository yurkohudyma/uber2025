package ua.hudyma.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.client.GeoClient;
import ua.hudyma.client.UserClient;
import ua.hudyma.domain.Ride;
import ua.hudyma.dto.RideRequestDto;
import ua.hudyma.dto.RouteDistanceResponseDto;
import ua.hudyma.dto.RouteDto;
import ua.hudyma.enums.RideStatus;
import ua.hudyma.repository.RideRepository;
import ua.hudyma.repository.VehicleRepository;

import java.math.BigDecimal;

import static java.lang.String.format;
import static ua.hudyma.enums.RideStatus.IN_PROGRESS;

@Service
@RequiredArgsConstructor
@Log4j2
public class RideService {
    @Value("${kafka.topic-name}")
    private String topic;
    @Value("${uber2025.base-price}")
    private BigDecimal basePrice;
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
        vehicle.getRideList().add(ride);
        var responseDtoTrack = geoClient
                .getDistanceWithTrack(rideRequestDto.routeDto());
        //todo engageVehicleMovementOnTrack();
        ride.setRideStatus(IN_PROGRESS);
        return true;
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
