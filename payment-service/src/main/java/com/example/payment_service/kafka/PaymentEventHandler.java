package com.example.payment_service.kafka;

import java.math.BigDecimal;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.example.payment_service.entity.Payment;
import com.example.payment_service.service.PaymentService;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class PaymentEventHandler {

    private final PaymentService paymentService;
    private final KafkaTemplate<String, String> kafka;
    private final ObjectMapper mapper = new ObjectMapper();

    public PaymentEventHandler(PaymentService paymentService, KafkaTemplate<String, String> kafka) {
        this.paymentService = paymentService;
        this.kafka = kafka;
    }

    @KafkaListener(topics = "inventory-reserved")
    public void onInventoryReserved(String msg) throws Exception {
        JsonNode e = mapper.readTree(msg);
        if (!e.has("orderId") || !e.has("amount")) return;

        long orderId = e.get("orderId").asLong();
        if (!paymentService.getPaymentsByOrderId(orderId).isEmpty()) return; // already paid

        String body = "{\"orderId\":" + orderId + "}";

        // Tell the order service that payment has started
        kafka.send("payment-processing", String.valueOf(orderId), body);

        Payment p = paymentService.processPayment(orderId,
                BigDecimal.valueOf(e.get("amount").asDouble()));

        String topic = "SUCCESS".equals(p.getPaymentStatus()) ? "payment-completed" : "payment-failed";
        kafka.send(topic, String.valueOf(orderId), body);
        System.out.println("Order " + orderId + " -> " + topic);
    }
}