package product_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import product_service.dto.CreateProductV2Request;
import product_service.dto.PatchProductV2Request;
import product_service.dto.ProductV2Response;
import product_service.dto.UpdateProductV2Request;
import product_service.entity.Product;
import product_service.repository.ProductRepository;

@Service
public class ProductV2Service {

    private final ProductRepository productRepository;

    public ProductV2Service(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // GET ALL PRODUCTS
    public List<ProductV2Response> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(this::convertToV2Response)
                .toList();
    }

    // GET PRODUCT BY ID
    public Optional<ProductV2Response> getProductById(Long id) {

        return productRepository.findById(id)
                .map(this::convertToV2Response);
    }

    // CREATE PRODUCT
    public ProductV2Response createProduct(
            CreateProductV2Request request) {

        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setStock(request.getStock());
        product.setStatus("ACTIVE");

        LocalDateTime now = LocalDateTime.now();

        product.setCreatedAt(now);
        product.setUpdatedAt(now);

        Product savedProduct =
                productRepository.save(product);

        return convertToV2Response(savedProduct);
    }

    // FULL UPDATE
    public Optional<ProductV2Response> updateProduct(
            Long id,
            UpdateProductV2Request request) {

        Optional<Product> optionalProduct =
                productRepository.findById(id);

        if (optionalProduct.isEmpty()) {
            return Optional.empty();
        }

        Product product = optionalProduct.get();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setStock(request.getStock());

        product.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct =
                productRepository.save(product);

        return Optional.of(
                convertToV2Response(updatedProduct)
        );
    }

    // PARTIAL UPDATE
    public Optional<ProductV2Response> patchProduct(
            Long id,
            PatchProductV2Request request) {

        Optional<Product> optionalProduct =
                productRepository.findById(id);

        if (optionalProduct.isEmpty()) {
            return Optional.empty();
        }

        Product product = optionalProduct.get();

        if (request.getName() != null) {
            product.setName(request.getName());
        }

        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }

        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }

        if (request.getCategory() != null) {
            product.setCategory(request.getCategory());
        }

        if (request.getStock() != null) {
            product.setStock(request.getStock());
        }

        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }

        product.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct =
                productRepository.save(product);

        return Optional.of(
                convertToV2Response(updatedProduct)
        );
    }

    // DELETE PRODUCT
    public boolean deleteProduct(Long id) {

        if (!productRepository.existsById(id)) {
            return false;
        }

        productRepository.deleteById(id);

        return true;
    }

    // CONVERT ENTITY TO V2 RESPONSE
    private ProductV2Response convertToV2Response(
            Product product) {

        return new ProductV2Response(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCategory(),
                product.getStock(),
                product.getStatus()
        );
    }
}