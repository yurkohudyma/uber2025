package ua.hudyma.geolocationservice.dto;

import java.util.List;

public record RouteDistanceResponseDto(
        Double distance,
        List<double[]> routePoints) {
}
