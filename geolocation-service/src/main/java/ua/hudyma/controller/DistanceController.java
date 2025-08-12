package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.hudyma.dto.RouteDistanceResponseDto;
import ua.hudyma.dto.RouteDto;

import static ua.hudyma.service.DistanceService.getDistance;

@RestController
@RequestMapping("/distance")
@RequiredArgsConstructor
@Log4j2
public class DistanceController {



    @PostMapping
    public ResponseEntity<RouteDistanceResponseDto> calculateDistance (@RequestBody RouteDto dto){
        return ResponseEntity.ok(getDistance(dto));
    }
}
