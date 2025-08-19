package ua.hudyma.rideservice.dto;

import java.util.List;

public record MapInfoDto
        (List<Double> vehicleCurrentPosition,
         List<Double> departure,
         List<Double> destination,
         List<List<Double>> toPAXroute,
         List<List<Double>> route) {}
