package order_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import order_service.client.UserServiceClient;
import order_service.dto.OrderWithUserResponse;
import order_service.dto.UserResponse;
import order_service.entity.Order;
import order_service.service.OrderService;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserServiceClient userServiceClient;

    public OrderController(OrderService orderService, UserServiceClient userServiceClient) {
        this.orderService = orderService;
        this.userServiceClient = userServiceClient;
    }

    // 1. Create Order
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order) {

        if (order.getUserId() == null) {
            return ResponseEntity.badRequest().body("userId is required");
        }

        if (order.getQuantity() == null || order.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body("quantity must be greater than 0");
        }

        try {
            Order savedOrder = orderService.createOrder(order);
            return ResponseEntity.ok(savedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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