package ua.hudyma.apigatewayservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/ride-service")
    public ResponseEntity<String> rideServiceFallback (){
        return ResponseEntity.ok("ride-service is currently UNAVALABLE");
    }
}
