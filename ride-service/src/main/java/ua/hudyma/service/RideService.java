package ua.hudyma.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.domain.Ride;
import ua.hudyma.dto.RideRequestDto;
import ua.hudyma.enums.RideStatus;
import ua.hudyma.repository.RideRepository;
import ua.hudyma.repository.VehicleRepository;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Log4j2
public class RideService {

    private final RideRepository rideRepository;
    private final VehicleRepository vehicleRepository;

    @Transactional
    public Ride addRide(RideRequestDto requestDto) {

        var driverId = requestDto.driverId();
        var paxId = requestDto.paxId();
        var vehicleId = requestDto.vehicleId();
        if (Objects.equals(driverId, paxId)) {
            throw new IllegalArgumentException("Driver and pax COULD NOT BE identical");
        }
        //todo fetch User from user-service and check if exists
        //userRepository.existsByDriverId(driverId);
        var ride = new Ride();
        ride.setDeparture(requestDto.departure());
        ride.setDestination(requestDto.destination());
        ride.setDriverId(driverId);
        ride.setRideStatus(RideStatus.APPROVED);

        //todo fetch Pax from user-service and check if exists
        //userRepository.existsByPaxId(paxId);
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
        return ride;
    }
}
