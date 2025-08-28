package ua.hudyma.apigatewayservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
    @GetMapping("/ride-service")
    public ResponseEntity<String> fallbackRide() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Ride Service is unavailable");
    }

    @GetMapping("/user-service")
    public ResponseEntity<String> fallbackUser() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("User Service is unavailable");
    }

    @GetMapping("/geolocation-service")
    public ResponseEntity<String> fallbackGeo() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Geolocation Service is unavailable");
    }

    @GetMapping("/rating-service")
    public ResponseEntity<String> fallbackRating() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Rating Service is unavailable");
    }

    @GetMapping("/payment-service")
    public ResponseEntity<String> fallbackPayment() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Payment Service is unavailable");
    }
}

