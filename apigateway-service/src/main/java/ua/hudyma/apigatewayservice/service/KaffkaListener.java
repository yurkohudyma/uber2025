package ua.hudyma.apigatewayservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KaffkaListener {

    @KafkaListener(topics = "uber2025", groupId = "uber2025_apigatewayservice")
    public void listen(String message) {
        System.out.println("Отримано повідомлення: " + message);
    }
}

