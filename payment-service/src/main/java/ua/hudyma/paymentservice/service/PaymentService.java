package ua.hudyma.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.paymentservice.client.RideClient;
import ua.hudyma.paymentservice.domain.Payment;
import ua.hudyma.paymentservice.dto.PaymentRequestDto;
import ua.hudyma.paymentservice.exception.PaymentAlreadyAppliedException;
import ua.hudyma.paymentservice.repository.PaymentRepository;

import static ua.hudyma.paymentservice.enums.PaymentType.CASH;
import static ua.hudyma.paymentservice.util.IdGenerator.generateId;

@Service
@RequiredArgsConstructor
@Log4j2
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final RideClient rideClient;


    public Payment applyPayment(PaymentRequestDto paymentRequestDto) {
        var rideId = paymentRequestDto.rideId();
        if (rideClient.paymentExists(rideId)){
            throw new PaymentAlreadyAppliedException
                    ("Ride obtains payment ID. Cannot apply another");
        }
        var payment = new Payment();
        payment.setId(generateId(8));
        payment.setRideId(rideId);
        payment.setPaymentType(paymentRequestDto.paymentType());
        payment.setAmount(paymentRequestDto.amount());
        paymentRepository.save(payment);
        var updatedPaymentDto = new PaymentRequestDto(
                paymentRequestDto.rideId(),
                payment.getPaymentType(),
                paymentRequestDto.amount(),
                payment.getId());
        try {
            rideClient.upsertRide(updatedPaymentDto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return payment;
    }
}
