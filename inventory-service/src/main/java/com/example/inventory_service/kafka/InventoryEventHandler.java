package com.example.inventory_service.kafka;

import com.example.inventory_service.service.InventoryService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventHandler {
    private final InventoryService service;
    private final KafkaTemplate<String, String> kafka;
    private final ObjectMapper mapper = new ObjectMapper();

    public InventoryEventHandler(InventoryService service, KafkaTemplate<String, String> kafka) {
        this.service = service; this.kafka = kafka;
    }

   @KafkaListener(topics = "order-created")
public void onOrderCreated(String msg) throws Exception {
    System.out.println("Received order-created: " + msg);
    JsonNode e = mapper.readTree(msg);
    if (!e.has("orderId") || !e.has("productId") || !e.has("quantity")) {
        System.out.println("Skipping: message needs orderId, productId, quantity");
        return;
    }
    long orderId = e.get("orderId").asLong();
    boolean ok = service.reserve(orderId, e.get("productId").asLong(), e.get("quantity").asInt());
    String topic = ok ? "inventory-reserved" : "inventory-failed";
    kafka.send(topic, String.valueOf(orderId), msg);
    System.out.println("Order " + orderId + " -> " + topic);
}
    @KafkaListener(topics = "payment-failed")
    public void onPaymentFailed(String msg) throws Exception {
        long orderId = mapper.readTree(msg).get("orderId").asLong();
        if (service.release(orderId)) {
            kafka.send("inventory-released", String.valueOf(orderId), msg);
            System.out.println("Order " + orderId + " -> inventory-released");
        }
    }

    @KafkaListener(topics = "payment-completed")
    public void onPaymentCompleted(String msg) throws Exception {
        service.complete(mapper.readTree(msg).get("orderId").asLong());
    }
}