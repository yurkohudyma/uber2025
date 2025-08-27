package ua.hudyma.paymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.hudyma.paymentservice.domain.Payment;

@Repository
public interface PaymentRepository extends JpaRepository <Payment, String> {
}
