package ua.hudyma.paymentservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Log4j2
public class RideClient {
    public final WebClient rideServiceWebClient;

    public boolean paymentExists(Long rideId) {
        return Boolean.TRUE.equals(rideServiceWebClient
                .get()
                .uri("/rides/paymentExists?rideId={rideId}", rideId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block());
    }
}
