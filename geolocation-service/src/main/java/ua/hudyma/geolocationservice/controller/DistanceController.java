package ua.hudyma.geolocationservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.hudyma.geolocationservice.dto.RouteDistanceResponseDto;
import ua.hudyma.geolocationservice.dto.RouteDto;
import ua.hudyma.geolocationservice.dto.RouteRandomPoint;
import ua.hudyma.geolocationservice.service.DistanceService;

import static ua.hudyma.geolocationservice.service.LocationRandomizer.randomizeLocation;

@RestController
@RequestMapping("/distance")
@RequiredArgsConstructor
@Log4j2
public class DistanceController {

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
