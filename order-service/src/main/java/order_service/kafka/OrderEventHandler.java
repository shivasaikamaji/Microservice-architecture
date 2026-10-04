package order_service.kafka;

import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import order_service.service.OrderService;

@Component
public class OrderEventHandler {

    // A status only moves forward, so late or duplicate events cannot move it back
    private static final Map<String, Integer> RANK = Map.of(
            "PENDING", 0,
            "INVENTORY_RESERVED", 1,
            "PAYMENT_PROCESSING", 2,
            "PAYMENT_FAILED", 3,
            "CONFIRMED", 4,
            "CANCELLED", 4);

    private final OrderService orderService;
    private final SagaEventPublisher publisher;
    private final ObjectMapper mapper = new ObjectMapper();

    public OrderEventHandler(OrderService orderService, SagaEventPublisher publisher) {
        this.orderService = orderService;
        this.publisher = publisher;
    }

    @KafkaListener(topics = "inventory-reserved")
    public void onInventoryReserved(String msg) throws Exception {
        advance(msg, "INVENTORY_RESERVED");
    }

    @KafkaListener(topics = "payment-processing")
    public void onPaymentProcessing(String msg) throws Exception {
        advance(msg, "PAYMENT_PROCESSING");
    }

    @KafkaListener(topics = "payment-completed")
    public void onPaymentCompleted(String msg) throws Exception {
        advance(msg, "CONFIRMED");
    }

    @KafkaListener(topics = "payment-failed")
    public void onPaymentFailed(String msg) throws Exception {
        advance(msg, "PAYMENT_FAILED");
    }

    @KafkaListener(topics = {"inventory-released", "inventory-failed"})
    public void onOrderCancelled(String msg) throws Exception {
        if (advance(msg, "CANCELLED")) {
            Long orderId = orderIdOf(msg);
            publisher.send("order-cancelled", String.valueOf(orderId), msg);
        }
    }

    private Long orderIdOf(String msg) throws Exception {
        JsonNode node = mapper.readTree(msg).get("orderId");
        return node == null ? null : node.asLong();
    }

    private boolean advance(String msg, String newStatus) throws Exception {
        Long orderId = orderIdOf(msg);
        if (orderId == null) return false;

        var order = orderService.getOrderById(orderId);
        if (order.isEmpty()) return false;

        int current = RANK.getOrDefault(order.get().getStatus(), -1);
        if (RANK.get(newStatus) <= current) return false;

        orderService.updateOrderStatus(orderId, newStatus);
        System.out.println("Order " + orderId + " -> " + newStatus);
        return true;
    }
}