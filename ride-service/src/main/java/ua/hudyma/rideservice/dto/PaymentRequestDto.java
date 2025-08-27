package ua.hudyma.rideservice.dto;

public record PaymentRequestDto(
        Long rideId,
        String paymentId
) {
}
