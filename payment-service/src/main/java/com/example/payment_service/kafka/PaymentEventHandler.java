package com.example.payment_service.kafka;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.example.payment_service.entity.Payment;
import com.example.payment_service.service.PaymentService;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class PaymentEventHandler {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventHandler.class);
    private static final String HEADER = "X-Correlation-Id";

    private final PaymentService paymentService;
    private final KafkaTemplate<String, String> kafka;
    private final ObjectMapper mapper = new ObjectMapper();

    public PaymentEventHandler(PaymentService paymentService, KafkaTemplate<String, String> kafka) {
        this.paymentService = paymentService;
        this.kafka = kafka;
    }

    @KafkaListener(topics = "inventory-reserved")
    public void onInventoryReserved(ConsumerRecord<String, String> record) throws Exception {

        Header header = record.headers().lastHeader(HEADER);
        String correlationId = (header != null)
                ? new String(header.value(), StandardCharsets.UTF_8) : "none";
        MDC.put("correlationId", correlationId);

        try {
            JsonNode e = mapper.readTree(record.value());
            if (!e.has("orderId") || !e.has("amount")) return;

            long orderId = e.get("orderId").asLong();
            log.info("Received inventory-reserved event for order {}", orderId);

            if (!paymentService.getPaymentsByOrderId(orderId).isEmpty()) return; // already paid

            String body = "{\"orderId\":" + orderId + "}";

            send("payment-processing", String.valueOf(orderId), body, correlationId);

            Payment p = paymentService.processPayment(orderId,
                    BigDecimal.valueOf(e.get("amount").asDouble()));

            String topic = "SUCCESS".equals(p.getPaymentStatus()) ? "payment-completed" : "payment-failed";
            if ("payment-failed".equals(topic)){
                log.error("payment FAILED for order {}: amount {} is above the 100000 limit",orderId, e.get("amount").asText());
            }
            send(topic, String.valueOf(orderId), body, correlationId);
            log.info("Order {} -> {}", orderId, topic);
        } finally {
            MDC.clear();
        }
    }

    private void send(String topic, String key, String body, String correlationId) {
        ProducerRecord<String, String> pr = new ProducerRecord<>(topic, key, body);
        pr.headers().add(HEADER, correlationId.getBytes(StandardCharsets.UTF_8));
        kafka.send(pr);
    }
}