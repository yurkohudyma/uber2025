package ua.hudyma.paymentservice.dto;

import ua.hudyma.paymentservice.enums.PaymentType;

import java.math.BigDecimal;

public record PaymentRequestDto(
        Long rideId,
        PaymentType paymentType,
        BigDecimal amount
) {
}
