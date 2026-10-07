package order_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import order_service.client.UserServiceClient;
import order_service.dto.OrderEvent;
import order_service.dto.OrderWithUserResponse;
import order_service.dto.UserResponse;
import order_service.entity.Order;
import order_service.service.KafkaProducerService;
import order_service.service.OrderService;
import order_service.service.IdempotencyService;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

   private final OrderService orderService;
    private final UserServiceClient userServiceClient;
    private final KafkaProducerService kafkaProducerService;
    private final IdempotencyService idempotencyService;

    public OrderController(OrderService orderService,
                            UserServiceClient userServiceClient,
                            KafkaProducerService kafkaProducerService,
                            IdempotencyService idempotencyService) {
        this.orderService = orderService;
        this.userServiceClient = userServiceClient;
        this.kafkaProducerService = kafkaProducerService;
        this.idempotencyService = idempotencyService;
    }
    // 1. Create Order (idempotent)
    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @RequestBody Order order) {

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return ResponseEntity.badRequest().body("Idempotency-Key header is required");
        }

        if (order.getUserId() == null) {
            return ResponseEntity.badRequest().body("userId is required");
        }

        if (order.getQuantity() == null || order.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body("quantity must be greater than 0");
        }

        Long userId = order.getUserId();

        // Step 6: duplicate request -> do NOT create another order
        boolean firstTime = idempotencyService.startProcessing(userId, idempotencyKey);
        if (!firstTime) {
            String status = idempotencyService.getStatus(userId, idempotencyKey);
            Long orderId = idempotencyService.getOrderId(userId, idempotencyKey);

            if ("COMPLETED".equals(status) && orderId != null) {
                var existing = orderService.getOrderById(orderId);
                if (existing.isPresent()) {
                    return ResponseEntity.ok()
                            .header("Idempotent-Replayed", "true")
                            .body(existing.get());
                }
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Original order could not be found");
            }
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A request with this Idempotency-Key is still being processed");
        }

        // Step 5: first request -> create the order and store the result
        Order savedOrder;
        try {
            savedOrder = orderService.createOrder(order);
        } catch (RuntimeException e) {
            idempotencyService.remove(userId, idempotencyKey); // allow a retry
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        idempotencyService.markCompleted(userId, idempotencyKey, savedOrder.getId());

        try {
            OrderEvent event = new OrderEvent(
                    savedOrder.getId(),
                    savedOrder.getUserId(),
                    savedOrder.getAmount(),
                    "ORDER_CREATED"
            );
            kafkaProducerService.sendOrderCreatedEvent(event);
        } catch (RuntimeException e) {
            System.out.println("Order saved but Kafka event failed: " + e.getMessage());
        }

        return ResponseEntity.ok(savedOrder);
    }

    // 2. Get Order by ID (plain, no user info)
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {

        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 2b. Get Order by ID WITH user details combined in (Step 7)
    @GetMapping("/{id}/with-user")
    public ResponseEntity<?> getOrderWithUser(@PathVariable Long id) {

        return orderService.getOrderById(id)
                .map(order -> {
                    try {
                        UserResponse user = userServiceClient.getUserDetails(order.getUserId());

                        OrderWithUserResponse response = new OrderWithUserResponse(
                                order.getId(),
                                order.getProductName(),
                                order.getQuantity(),
                                order.getAmount(),
                                order.getStatus(),
                                user
                        );

                        return ResponseEntity.ok(response);
                    } catch (RuntimeException e) {
                        // Step 9: User Service down or user missing -> fail gracefully, not a raw 500
                        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body(e.getMessage());
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Get User Orders
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    // 4. Update Order Status
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        String status = request.get("status");

        return orderService.updateOrderStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. Delete / Cancel Order
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {

        if (orderService.deleteOrder(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}