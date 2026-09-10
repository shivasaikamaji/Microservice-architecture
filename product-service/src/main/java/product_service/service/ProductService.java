package product_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import product_service.dto.CreateProductRequest;
import product_service.dto.PatchProductRequest;
import product_service.dto.ProductResponse;
import product_service.dto.UpdateProductRequest;
import product_service.entity.Product;
import product_service.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Get products with pagination, sorting and filtering
    public Page<ProductResponse> getProductsWithFilters(
            int page,
            int size,
            String sortBy,
            String direction,
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice) {

        Sort sort;

        if (direction.equalsIgnoreCase("desc")) {
            sort = Sort.by(sortBy).descending();
        } else {
            sort = Sort.by(sortBy).ascending();
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Product> specification =
        (root, query, criteriaBuilder) ->
                criteriaBuilder.conjunction();

        if (category != null && !category.isBlank()) {
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    criteriaBuilder.lower(root.get("category")),
                                    category.toLowerCase()
                            )
            );
        }

        if (minPrice != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.greaterThanOrEqualTo(
                                    root.get("price"),
                                    minPrice
                            )
            );
        }

        if (maxPrice != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.lessThanOrEqualTo(
                                    root.get("price"),
                                    maxPrice
                            )
            );
        }

        return productRepository.findAll(specification, pageable)
                .map(this::convertToResponse);
    }

    // Get product by ID
    public Optional<ProductResponse> getProductById(Long id) {

        return productRepository.findById(id)
                .map(this::convertToResponse);
    }

    // Create product
    public ProductResponse createProduct(
            CreateProductRequest request) {

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

        return convertToResponse(savedProduct);
    }

    // Update product
    public Optional<ProductResponse> updateProduct(
            Long id,
            UpdateProductRequest request) {

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
                convertToResponse(updatedProduct)
        );
    }

    // Patch product
    public Optional<ProductResponse> patchProduct(
            Long id,
            PatchProductRequest request) {

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
            product.setDescription(
                    request.getDescription()
            );
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
                convertToResponse(updatedProduct)
        );
    }

    // Delete product
    public boolean deleteProduct(Long id) {

        if (!productRepository.existsById(id)) {
            return false;
        }

        productRepository.deleteById(id);

        return true;
    }

    // Convert Product entity to ProductResponse
    private ProductResponse convertToResponse(
            Product product) {

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getStock(),
                product.getStatus(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}