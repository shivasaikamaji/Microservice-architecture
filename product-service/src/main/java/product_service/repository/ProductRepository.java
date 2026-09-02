package product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import product_service.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}