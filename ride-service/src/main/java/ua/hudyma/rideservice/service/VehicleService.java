package ua.hudyma.rideservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.rideservice.domain.Ride;
import ua.hudyma.rideservice.domain.Vehicle;
import ua.hudyma.rideservice.dto.CurPosReqDto;
import ua.hudyma.rideservice.dto.RoutePoint;
import ua.hudyma.rideservice.repository.VehicleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public Vehicle addVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public boolean existsById(Long vehicleId) {
        return vehicleRepository.existsById(vehicleId);
    }

    public List<Ride> getVehicleRides(Long vehicleId) {
        var vehicle = vehicleRepository.findById(vehicleId).orElseThrow();
        return vehicle.getRideList();
    }

    @Transactional
    public RoutePoint updateCurrentPosition(CurPosReqDto dto) {
        var vehicle = vehicleRepository.findById(dto.vehicleId()).orElseThrow();
        vehicle.setCurrentPosition(dto.curPos());
        return vehicle.getCurrentPosition();
    }
}
