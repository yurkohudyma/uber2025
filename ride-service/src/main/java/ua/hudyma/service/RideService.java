package ua.hudyma.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
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
import java.util.Objects;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Log4j2
public class RideService {
    @Value("${kafka.topic-name}")
    public String topic;
    private final RideRepository rideRepository;
    private final VehicleRepository vehicleRepository;
    private final KaffkaProducer kaffkaProducer;
    private final UserClient userClient;
    private final GeoClient geoClient;

    @Transactional
    public Ride addRide(RideRequestDto requestDto) {

        var driverId = requestDto.driverId();
        var paxId = requestDto.paxId();
        var vehicleId = requestDto.vehicleId();
        if (Objects.equals(driverId, paxId)) {
            throw new IllegalArgumentException("Driver and pax COULD NOT BE identical");
        }
        if (!userClient.driverExists(driverId)){
            var msg = format("User with driverId = %s has NOT BEEN FOUND", driverId);
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        var ride = new Ride();
        ride.setDeparture(requestDto.departure());
        ride.setDestination(requestDto.destination());
        ride.setDriverId(driverId);
        ride.setRideStatus(RideStatus.REQUESTED);

        if (!userClient.paxExists(paxId)){
            var msg = format("User with paxId = %s has NOT BEEN FOUND", paxId);
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        ride.setPaxId(paxId);

        var vehicle = vehicleRepository.findById(vehicleId).orElseThrow();
        ride.setVehicle(vehicle);
        var vehiclePriceCoeff = vehicle.getVehicleClass().getPriceCoefficient();


        //todo get distance from geoservice
        ride.setRidePrice(BigDecimal.valueOf(2.84029)// insert calculated data
                .multiply(vehiclePriceCoeff));
        rideRepository.save(ride);
        log.info("ride {} has been saved", ride.getId());
        vehicle.getRideList().add(ride);
        var msg = format("ride %s requested", ride.getId());
        kaffkaProducer.sendMessage(topic, msg);
        return ride;
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

    public Flux<RouteDistanceResponseDto> getDistance (RouteDto dto){
        return geoClient.getDistance(dto);
    }
}
