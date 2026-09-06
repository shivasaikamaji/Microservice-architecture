package order_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import order_service.entity.Order;
import order_service.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // 1. Get all orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 2. Get order by ID
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    // 3. Get orders by user ID
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    // 4. Create order
    public Order createOrder(Order order) {

        // Set default status when creating a new order
        if (order.getStatus() == null || order.getStatus().isBlank()) {
            order.setStatus("CREATED");
        }

        return orderRepository.save(order);
    }

    // 5. Update order
    public Optional<Order> updateOrder(Long id, Order orderDetails) {

        return orderRepository.findById(id).map(order -> {

            order.setUserId(orderDetails.getUserId());
            order.setProductName(orderDetails.getProductName());
            order.setQuantity(orderDetails.getQuantity());
            order.setAmount(orderDetails.getAmount());

            if (orderDetails.getStatus() != null) {
                order.setStatus(orderDetails.getStatus());
            }

            return orderRepository.save(order);
        });
    }

    // 6. Update order status
    public Optional<Order> updateOrderStatus(Long id, String status) {

        return orderRepository.findById(id).map(order -> {

            order.setStatus(status);

            return orderRepository.save(order);
        });
    }

    // 7. Cancel order
    public Optional<Order> cancelOrder(Long id) {

        return orderRepository.findById(id).map(order -> {

            order.setStatus("CANCELLED");

            return orderRepository.save(order);
        });
    }

    // 8. Delete order
    public boolean deleteOrder(Long id) {

        if (!orderRepository.existsById(id)) {
            return false;
        }

        orderRepository.deleteById(id);
        return true;
    }
}