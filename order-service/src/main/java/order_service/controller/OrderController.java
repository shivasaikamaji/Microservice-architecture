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

    public OrderController(
            OrderService orderService,
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
            @RequestHeader(value = "Idempotency-Key", required = false)
            String idempotencyKey,
            @RequestBody Order order) {

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Idempotency-Key header is required");
        }

        if (order.getUserId() == null) {
            return ResponseEntity.badRequest()
                    .body("userId is required");
        }

        if (order.getQuantity() == null || order.getQuantity() <= 0) {
            return ResponseEntity.badRequest()
                    .body("quantity must be greater than 0");
        }

        Long userId = order.getUserId();

        // Step 6: Create a fingerprint input for the request.
        String requestData =
                String.valueOf(order.getUserId()) + "|" +
                String.valueOf(order.getProductName()) + "|" +
                String.valueOf(order.getQuantity()) + "|" +
                String.valueOf(order.getAmount());
        System.out.println("DEBUG requestData: [" + requestData + "]");

        // Check whether this is the first request with this key.
        boolean firstTime = idempotencyService.startProcessing(
                userId, idempotencyKey, requestData);

        // Reject reuse of the same key with different request data.
        if (!firstTime && !idempotencyService.isSameRequest(
                userId, idempotencyKey, requestData)) {

            return ResponseEntity.badRequest()
                    .body("Idempotency-Key was already used with a different request");
        }

        // If this is a duplicate request, return the existing result.
        if (!firstTime) {

            String status =
                    idempotencyService.getStatus(userId, idempotencyKey);

            Long orderId =
                    idempotencyService.getOrderId(userId, idempotencyKey);

           if ("COMPLETED".equals(status) && orderId != null) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
            .body("This Idempotency-Key has already been used. Please use a new key.");
}

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A request with this Idempotency-Key is still being processed");
        }

        // Step 5: Create the order for the first request.
        Order savedOrder;

        try {
            savedOrder = orderService.createOrder(order);
        } catch (RuntimeException e) {

            idempotencyService.remove(userId, idempotencyKey);

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }

        // Save the completed result in Redis.
        idempotencyService.markCompleted(
                userId, idempotencyKey, savedOrder.getId());

        // Publish the order-created event to Kafka.
        try {
            OrderEvent event = new OrderEvent(
                    savedOrder.getId(),
                    savedOrder.getUserId(),
                    savedOrder.getAmount(),
                    "ORDER_CREATED"
            );

            kafkaProducerService.sendOrderCreatedEvent(event);

        } catch (RuntimeException e) {
            System.out.println(
                    "Order saved but Kafka event failed: " + e.getMessage());
        }

        return ResponseEntity.ok(savedOrder);
    }

    // 2. Get Order by ID.
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {

        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 2b. Get Order by ID with user details.
    @GetMapping("/{id}/with-user")
    public ResponseEntity<?> getOrderWithUser(@PathVariable Long id) {

        return orderService.getOrderById(id)
                .map(order -> {
                    try {
                        UserResponse user =
                                userServiceClient.getUserDetails(order.getUserId());
if (user == null) {
        return ResponseEntity
        .status(HttpStatus.SERVICE_UNAVAILABLE)
        .body("User Service is currently unavailable Please try again later.");
}
                        OrderWithUserResponse response =
                                new OrderWithUserResponse(
                                        order.getId(),
                                        order.getProductName(),
                                        order.getQuantity(),
                                        order.getAmount(),
                                        order.getStatus(),
                                        user
                                );

                        return ResponseEntity.ok(response);

                    } catch (RuntimeException e) {
                        return ResponseEntity
                                .status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body(e.getMessage());
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Get all orders for a user.
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                orderService.getOrdersByUserId(userId));
    }

    // 4. Update order status.
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        String status = request.get("status");

        return orderService.updateOrderStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. Delete or cancel an order.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {

        if (orderService.deleteOrder(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}