package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.domain.Ride;
import ua.hudyma.dto.RideRequestDto;
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
    public boolean existsUserByPaxId (@RequestParam String paxId){
        return rideService.existsByPaxId(paxId);
    }
}
