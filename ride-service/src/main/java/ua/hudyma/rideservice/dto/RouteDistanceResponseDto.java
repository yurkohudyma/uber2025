package ua.hudyma.rideservice.dto;

import java.util.List;

public record RouteDistanceResponseDto(
        Double distance,
        List<double[]> routePoints) {
}
