package ua.hudyma.paymentservice.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import ua.hudyma.paymentservice.enums.PaymentType;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "payments")
@Data
public class Payment {

    @Id
    private String id;
    @Column(nullable = false)
    private Long rideId;
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    PaymentType paymentType;
    @PositiveOrZero
    BigDecimal amount;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    @CreationTimestamp
    private Date paidOn;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    @CreationTimestamp
    private Date refundedOn;

}
