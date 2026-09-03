package order_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import order_service.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

}