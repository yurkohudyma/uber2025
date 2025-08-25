package ua.hudyma.geolocationservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.geolocationservice.dto.RouteDistanceResponseDto;
import ua.hudyma.geolocationservice.dto.RouteDto;
import ua.hudyma.geolocationservice.dto.RouteRandomPoint;
import ua.hudyma.geolocationservice.service.DistanceService;
import ua.hudyma.geolocationservice.service.GraphHopperService;

import static ua.hudyma.geolocationservice.service.LocationRandomizer.randomizeLocation;

@RestController
@RequestMapping("/distance")
@RequiredArgsConstructor
@Log4j2
public class DistanceController {

    private final GraphHopperService graphHopperService;

    @GetMapping("checkVehiclePassable")
    public ResponseEntity<Boolean> checkPassabilityForVehicle (@RequestBody RouteDto routeDto){
        return ResponseEntity.ok(graphHopperService.isPointAccessibleForVehicle(routeDto));
    }

    @PostMapping("checkVehiclePassableAPI")
    public ResponseEntity<StringBuilder> checkRoutePassableForVehicleAPI (@RequestBody RouteDto routeDto){
        return ResponseEntity.ok(graphHopperService.isRoutePassable((routeDto)));
    }

    @PostMapping
    public ResponseEntity<RouteDistanceResponseDto> calcDistanceWithTrack(
            @RequestBody RouteDto dto) {
        return ResponseEntity.ok(
                DistanceService.getDistance(dto, true));
    }

    @PostMapping("/noTrack")
    public ResponseEntity<RouteDistanceResponseDto> calcDistanceNoTrack(
            @RequestBody RouteDto dto) {
        return ResponseEntity.ok(
                DistanceService.getDistance(dto, false));
    }

    @PostMapping("/getRandom")
    private ResponseEntity<RouteRandomPoint> getRandomPoint(
            @RequestBody RouteRandomPoint epicentrePoint) {
        return ResponseEntity.ok(
                randomizeLocation(epicentrePoint));
    }
}
