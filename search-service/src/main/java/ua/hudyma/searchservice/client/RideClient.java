package ua.hudyma.searchservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Log4j2
public class RideClient {

    public final WebClient rideServiceWebClient;

    public String findString(String str, String userId) {
        return rideServiceWebClient
                .get()
                .uri("/search?str={str}&userId={userId}", str, userId)
                .retrieve()
                .bodyToMono(String.class)
                .block();

    }

    public boolean vehicleExists (Long vehicleId){
        return Boolean.TRUE.equals(rideServiceWebClient
                .get()
                .uri("/rides/vehicleExists?vehicleId={vehicleId}", vehicleId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block());
    }
}
