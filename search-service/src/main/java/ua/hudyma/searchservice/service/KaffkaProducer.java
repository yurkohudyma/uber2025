package ua.hudyma.searchservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KaffkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    //private final KafkaTemplate<String, Object> kafkaTemplateObj;

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }

    /*public void sendMessage(String topic, Object obj) {
        kafkaTemplateObj.send(topic, obj);
    }*/
}

