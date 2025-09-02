package ua.hudyma.searchservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ua.hudyma.rideservice.dto.RouteDistanceResponseDto;
import ua.hudyma.rideservice.dto.RouteDto;

@Service
@RequiredArgsConstructor
@Log4j2
public class GeoClient {
    private final WebClient geoServiceWebClient;

    public RouteDistanceResponseDto getDistance(RouteDto dto, boolean addTrack){
        var uri = addTrack ? "/distance" : "/distance/noTrack";
        return geoServiceWebClient
                .post()
                .uri(uri)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(RouteDistanceResponseDto.class)
                .block();
    }
}