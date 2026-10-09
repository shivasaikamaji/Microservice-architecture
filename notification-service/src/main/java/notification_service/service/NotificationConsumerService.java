package notification_service.service;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumerService {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumerService.class);

    @KafkaListener(topics = "order-created", groupId = "notification-service-group")
    public void consumeOrderCreatedEvent(ConsumerRecord<String, Map<String, Object>> record) {

        Header header = record.headers().lastHeader("X-Correlation-Id");
        String correlationId = (header != null)
                ? new String(header.value(), StandardCharsets.UTF_8) : "none";
        MDC.put("correlationId", correlationId);

        try {
            Map<String, Object> event = record.value();
            log.info("Received ORDER_CREATED event: {}", event);
            log.info("Sending notification to user {} about order {} worth {}",
                    event.get("userId"), event.get("orderId"), event.get("amount"));
        } finally {
            MDC.clear();
        }
    }
}