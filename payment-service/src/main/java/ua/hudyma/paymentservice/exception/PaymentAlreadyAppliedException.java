package ua.hudyma.paymentservice.exception;

public class PaymentAlreadyAppliedException extends RuntimeException {
    public PaymentAlreadyAppliedException(String message) {
        super(message);
    }
}
