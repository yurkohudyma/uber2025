package ua.hudyma.rideservice.dto;

import ua.hudyma.rideservice.constants.TrackDirection;

public record RouteDto(RoutePoint departure,
                       RoutePoint destination,
                       TrackDirection trackDirection) {
}
