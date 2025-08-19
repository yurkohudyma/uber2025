package ua.hudyma.rideservice.dto;

import java.util.List;

public record VehicleCurrentPosMapDto(
        List<Double> currentPosition) {
}
