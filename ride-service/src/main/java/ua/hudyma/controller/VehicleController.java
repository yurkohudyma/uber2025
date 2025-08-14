package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.domain.Ride;
import ua.hudyma.domain.Vehicle;
import ua.hudyma.dto.CurPosReqDto;
import ua.hudyma.dto.RoutePoint;
import ua.hudyma.service.VehicleService;

import java.util.List;

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

    @GetMapping("/getRides/{vehicleId}")
    public ResponseEntity<List<Ride>> getAllVehicleRides (@PathVariable Long vehicleId){
        return ResponseEntity.ok(vehicleService.getVehicleRides (vehicleId));
    }

    @GetMapping("updCurPos")
    public ResponseEntity<RoutePoint> updateCurrentPosition (@RequestBody CurPosReqDto dto){
        return ResponseEntity.ok(vehicleService.updateCurrentPosition (dto));
    }
}
