package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import ua.hudyma.domain.Ride;
import ua.hudyma.dto.RideRequestDto;
import ua.hudyma.dto.RouteDistanceResponseDto;
import ua.hudyma.dto.RouteDto;
import ua.hudyma.dto.RoutePoint;
import ua.hudyma.service.RideService;

@RestController
@RequestMapping("/rides")
@Log4j2
@RequiredArgsConstructor

public class RideController {

    private final RideService rideService;

    @PostMapping
    public ResponseEntity<Ride> addRide (@RequestBody RideRequestDto dto){
        return ResponseEntity.ok(rideService.addRide (dto));
    }

    @GetMapping("/paxExists")
    public boolean userExistsByPaxId (@RequestParam String paxId){
        return rideService.existsByPaxId(paxId);
    }

    @GetMapping("/driverExists")
    public boolean userExistsDriveByDriverId (@RequestParam String driverId){
        return rideService.existsByDriverId(driverId);
    }

    @PostMapping("/distance")
    public ResponseEntity<Flux<RouteDistanceResponseDto>> getDistance
            (@RequestBody RouteDto dto){
        var result = rideService.getDistance(dto);
        return ResponseEntity.ok(result);
    }
}
