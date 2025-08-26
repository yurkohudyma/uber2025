package ua.hudyma.ratingservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KaffkaListener {

    @KafkaListener(topics = "uber2025", groupId = "uber2025_ratingservice")
    public void listen(String message) {
        System.out.println("Отримано повідомлення: " + message);
    }
}

