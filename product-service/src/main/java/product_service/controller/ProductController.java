package product_service.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import product_service.entity.Product;
import product_service.repository.ProductRepository;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // =========================================================
    // GET ALL PRODUCTS
    // GET /api/v1/products
    // =========================================================
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {

        List<Product> products = productRepository.findAll();

        return ResponseEntity.ok(products);
    }

    // =========================================================
    // GET PRODUCT BY ID
    // GET /api/v1/products/{id}
    // =========================================================
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {

        Optional<Product> product = productRepository.findById(id);

        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Product not found with id: " + id);
    }

    // =========================================================
    // CREATE PRODUCT
    // POST /api/v1/products
    // =========================================================
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {

        if (product.getName() == null || product.getName().trim().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Product name is required");
        }

        if (product.getPrice() == null || product.getPrice().signum() < 0) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Product price must be greater than or equal to 0");
        }

        if (product.getStock() == null || product.getStock() < 0) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Product stock must be greater than or equal to 0");
        }

        Product savedProduct = productRepository.save(product);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedProduct);
    }

    // =========================================================
    // COMPLETE UPDATE
    // PUT /api/v1/products/{id}
    // =========================================================
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestBody Product productDetails) {

        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Product not found with id: " + id);
        }

        if (productDetails.getName() == null
                || productDetails.getName().trim().isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Product name is required");
        }

        if (productDetails.getPrice() == null
                || productDetails.getPrice().signum() < 0) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Product price must be greater than or equal to 0");
        }

        if (productDetails.getStock() == null
                || productDetails.getStock() < 0) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Product stock must be greater than or equal to 0");
        }

        Product product = optionalProduct.get();

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setCategory(productDetails.getCategory());
        product.setStock(productDetails.getStock());
        product.setStatus(productDetails.getStatus());

        Product updatedProduct = productRepository.save(product);

        return ResponseEntity.ok(updatedProduct);
    }

    // =========================================================
    // PARTIAL UPDATE
    // PATCH /api/v1/products/{id}
    // =========================================================
    @PatchMapping("/{id}")
    public ResponseEntity<?> patchProduct(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {

        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Product not found with id: " + id);
        }

        if (updates == null || updates.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("At least one field is required for PATCH");
        }

        Product product = optionalProduct.get();

        // Update name
        if (updates.containsKey("name")) {
            Object name = updates.get("name");

            if (name == null || name.toString().trim().isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Product name cannot be empty");
            }

            product.setName(name.toString());
        }

        // Update description
        if (updates.containsKey("description")) {
            Object description = updates.get("description");

            if (description != null) {
                product.setDescription(description.toString());
            }
        }

        // Update category
        if (updates.containsKey("category")) {
            Object category = updates.get("category");

            if (category != null) {
                product.setCategory(category.toString());
            }
        }

        // Update status
        if (updates.containsKey("status")) {
            Object status = updates.get("status");

            if (status != null) {
                product.setStatus(status.toString());
            }
        }

        // Update price
        if (updates.containsKey("price")) {
            Object priceValue = updates.get("price");

            try {
                if (priceValue == null) {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body("Price cannot be null");
                }

                java.math.BigDecimal price =
                        new java.math.BigDecimal(priceValue.toString());

                if (price.signum() < 0) {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body("Price must be greater than or equal to 0");
                }

                product.setPrice(price);

            } catch (NumberFormatException e) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Invalid price value");
            }
        }

        // Update stock
        if (updates.containsKey("stock")) {
            Object stockValue = updates.get("stock");

            try {
                if (stockValue == null) {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body("Stock cannot be null");
                }

                Integer stock = Integer.valueOf(stockValue.toString());

                if (stock < 0) {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body("Stock must be greater than or equal to 0");
                }

                product.setStock(stock);

            } catch (NumberFormatException e) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Invalid stock value");
            }
        }

        Product updatedProduct = productRepository.save(product);

        return ResponseEntity.ok(updatedProduct);
    }

    // =========================================================
    // DELETE PRODUCT
    // DELETE /api/v1/products/{id}
    // =========================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {

        Optional<Product> product = productRepository.findById(id);

        if (product.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Product not found with id: " + id);
        }

        productRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}