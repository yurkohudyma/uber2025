package ua.hudyma.paymentservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.hudyma.paymentservice.dto.PaymentRequestDto;
import ua.hudyma.paymentservice.dto.PaymentResponseDto;
import ua.hudyma.paymentservice.service.PaymentService;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponseDto> applyPayment (
            @RequestBody PaymentRequestDto paymentRequestDto){
        var payment = paymentService
                .applyPayment(paymentRequestDto);
        return ResponseEntity.ok(
                new PaymentResponseDto(payment.getPaymentType(), payment.getAmount()));
    }
}
