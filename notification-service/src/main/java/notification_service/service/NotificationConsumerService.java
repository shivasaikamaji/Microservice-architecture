package notification_service.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationConsumerService {

    @KafkaListener(topics = "order-created", groupId = "notification-service-group")
    public void consumeOrderCreatedEvent(Map<String, Object> event) {
        System.out.println("Received ORDER_CREATED event: " + event);

        Object orderId = event.get("orderId");
        Object userId = event.get("userId");
        Object amount = event.get("amount");

        System.out.println("Sending notification to user " + userId +
                " about order " + orderId + " worth " + amount);
    }
}