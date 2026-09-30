package order_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final String TOPIC = "order-created";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrderCreatedEvent(Object orderEvent) {
        kafkaTemplate.send(TOPIC, orderEvent);
    }
}