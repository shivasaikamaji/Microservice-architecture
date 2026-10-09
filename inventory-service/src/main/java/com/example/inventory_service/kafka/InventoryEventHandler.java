package com.example.inventory_service.kafka;

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

import com.example.inventory_service.service.InventoryService;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class InventoryEventHandler {

    private static final Logger log = LoggerFactory.getLogger(InventoryEventHandler.class);
    private static final String HEADER = "X-Correlation-Id";

    private final InventoryService service;
    private final KafkaTemplate<String, String> kafka;
    private final ObjectMapper mapper = new ObjectMapper();

    public InventoryEventHandler(InventoryService service, KafkaTemplate<String, String> kafka) {
        this.service = service;
        this.kafka = kafka;
    }

    // read the ID from the Kafka message and put it in the log context
    private String startTrace(ConsumerRecord<String, String> record) {
        Header h = record.headers().lastHeader(HEADER);
        String id = (h != null) ? new String(h.value(), StandardCharsets.UTF_8) : "none";
        MDC.put("correlationId", id);
        return id;
    }

    // send a message and attach the same ID
    private void send(String topic, String key, String msg, String id) {
        ProducerRecord<String, String> pr = new ProducerRecord<>(topic, key, msg);
        pr.headers().add(HEADER, id.getBytes(StandardCharsets.UTF_8));
        kafka.send(pr);
    }

    @KafkaListener(topics = "order-created")
    public void onOrderCreated(ConsumerRecord<String, String> record) throws Exception {
        String id = startTrace(record);
        try {
            String msg = record.value();
            log.info("Received order-created: {}", msg);
            JsonNode e = mapper.readTree(msg);
            if (!e.has("orderId") || !e.has("productId") || !e.has("quantity")) {
                log.warn("Skipping: message needs orderId, productId, quantity");
                return;
            }
            long orderId = e.get("orderId").asLong();
            boolean ok = service.reserve(orderId, e.get("productId").asLong(), e.get("quantity").asInt());
            String topic = ok ? "inventory-reserved" : "inventory-failed";
            send(topic, String.valueOf(orderId), msg, id);
            log.info("Order {} -> {}", orderId, topic);
        } finally {
            MDC.clear();
        }
    }

    @KafkaListener(topics = "payment-failed")
    public void onPaymentFailed(ConsumerRecord<String, String> record) throws Exception {
        String id = startTrace(record);
        try {
            String msg = record.value();
            long orderId = mapper.readTree(msg).get("orderId").asLong();
            if (service.release(orderId)) {
                send("inventory-released", String.valueOf(orderId), msg, id);
                log.info("Order {} -> inventory-released", orderId);
            }
        } finally {
            MDC.clear();
        }
    }

    @KafkaListener(topics = "payment-completed")
    public void onPaymentCompleted(ConsumerRecord<String, String> record) throws Exception {
        startTrace(record);
        try {
            service.complete(mapper.readTree(record.value()).get("orderId").asLong());
            log.info("Payment completed, inventory finalised");
        } finally {
            MDC.clear();
        }
    }
}