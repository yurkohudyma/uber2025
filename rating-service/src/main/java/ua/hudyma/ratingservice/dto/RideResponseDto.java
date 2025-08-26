package ua.hudyma.ratingservice.dto;

public record RideResponseDto(
        Long rideId,
        String driverId,
        String paxId) {}
