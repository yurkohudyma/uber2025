package ua.hudyma.paymentservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KaffkaListener {

    @KafkaListener(topics = "uber2025", groupId = "uber2025_paymentservice")
    public void listen(String message) {
        System.out.println("Отримано повідомлення: " + message);
    }
}

