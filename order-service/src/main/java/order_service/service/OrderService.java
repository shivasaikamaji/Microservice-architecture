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

    // Get all orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Get order by ID
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    // Get orders by user ID
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    // Create order
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    // Update order
    public Optional<Order> updateOrder(Long id, Order orderDetails) {
        return orderRepository.findById(id).map(order -> {
            order.setUserId(orderDetails.getUserId());
            order.setProductId(orderDetails.getProductId());
            order.setQuantity(orderDetails.getQuantity());
            

            return orderRepository.save(order);
        });
    }

    // Delete order
    public boolean deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            return false;
        }

        orderRepository.deleteById(id);
        return true;
    }
}