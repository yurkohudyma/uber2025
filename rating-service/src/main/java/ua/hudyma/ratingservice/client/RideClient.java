package ua.hudyma.ratingservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ua.hudyma.ratingservice.dto.RideResponseDto;

@Service
@RequiredArgsConstructor
@Log4j2
public class RideClient {
    public final WebClient rideServiceWebClient;

    public RideResponseDto getRideDto (Long rideId){
        return rideServiceWebClient
                .get()
                .uri("/rides/{rideId}", rideId)
                .retrieve()
                .bodyToMono(RideResponseDto.class)
                .block();
    }
}
