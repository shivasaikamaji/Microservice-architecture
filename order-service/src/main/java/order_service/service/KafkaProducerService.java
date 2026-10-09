package order_service.service;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);
    private static final String TOPIC = "order-created";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrderCreatedEvent(Object orderEvent) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(TOPIC, orderEvent);

        String correlationId = MDC.get("correlationId");
        if (correlationId != null) {
            record.headers().add("X-Correlation-Id",
                    correlationId.getBytes(StandardCharsets.UTF_8));
        }

        kafkaTemplate.send(record);
        log.info("Published event to topic {}", TOPIC);
    }
}