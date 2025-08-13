package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.hudyma.domain.Vehicle;
import ua.hudyma.service.VehicleService;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
@Log4j2
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<Vehicle> addVehicle (@RequestBody Vehicle vehicle){
        return ResponseEntity.ok(vehicleService.addVehicle (vehicle));
    }
}
