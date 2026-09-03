package order_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import order_service.entity.Order;
import order_service.repository.OrderRepository;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Test Order Service
    @GetMapping
    public String getOrders() {
        return "Order Service is working";
    }

    // Create Order
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order savedOrder = orderRepository.save(order);
        return ResponseEntity.ok(savedOrder);
    }

    // Get orders by user ID
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getOrdersByUser(@PathVariable Long id) {
        return ResponseEntity.ok(orderRepository.findByUserId(id));
    }
}