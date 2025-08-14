package ua.hudyma.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.controller.VehicleController;
import ua.hudyma.domain.Ride;
import ua.hudyma.domain.Vehicle;
import ua.hudyma.dto.CurPosReqDto;
import ua.hudyma.dto.RoutePoint;
import ua.hudyma.repository.VehicleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public Vehicle addVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
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
