package ua.hudyma.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.controller.VehicleController;
import ua.hudyma.domain.Vehicle;
import ua.hudyma.repository.VehicleRepository;

@Service
@RequiredArgsConstructor
@Log4j2
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public Vehicle addVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }
}
