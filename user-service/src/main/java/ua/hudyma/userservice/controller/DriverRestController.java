package ua.hudyma.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.userservice.client.RideClient;
import ua.hudyma.userservice.repository.DriverRepository;

@RestController
@RequestMapping("/drivers")
@RequiredArgsConstructor
@Log4j2
public class DriverRestController {
    private final DriverRepository driverRepository;
    private final RideClient rideClient;

    @DeleteMapping("/all")
    public void deleteAllDrivers (){
        var allDrivers = driverRepository.findAll();
        driverRepository.deleteAll(allDrivers);
    }

    @GetMapping("/vehicleExists")
    public boolean existsVehicleById (@RequestParam Long vehicleId){
        return rideClient.vehicleExists(vehicleId);
    }
}
