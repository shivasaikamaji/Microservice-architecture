package product_service.controller;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import product_service.dto.CreateProductRequest;
import product_service.dto.ErrorResponse;
import product_service.dto.PatchProductRequest;
import product_service.dto.ProductResponse;
import product_service.dto.UpdateProductRequest;
import product_service.service.ProductService;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // =====================================================
    // GET ALL PRODUCTS
    // Pagination + Sorting + Filtering
    // =====================================================

    @GetMapping
    public ResponseEntity<?> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        // Validate page
        if (page < 0) {
            return badRequest("Page number cannot be negative");
        }

        // Validate size
        if (size <= 0) {
            return badRequest("Page size must be greater than 0");
        }

        // Validate price
        if (minPrice != null
                && minPrice.compareTo(BigDecimal.ZERO) < 0) {

            return badRequest("Minimum price cannot be negative");
        }

        if (maxPrice != null
                && maxPrice.compareTo(BigDecimal.ZERO) < 0) {

            return badRequest("Maximum price cannot be negative");
        }

        // Validate price range
        if (minPrice != null
                && maxPrice != null
                && minPrice.compareTo(maxPrice) > 0) {

            return badRequest(
                    "Minimum price cannot be greater than maximum price"
            );
        }

        // Read sorting parameters
        String[] sortParts = sort.split(",");

        String sortBy = sortParts[0];
        String direction = "asc";

        if (sortParts.length > 1) {
            direction = sortParts[1];
        }

        // Validate sort field
        if (!isValidSortField(sortBy)) {

            return badRequest(
                    "Invalid sort field. Allowed fields: id, name, price, category, stock, createdAt, updatedAt"
            );
        }

        // Validate sort direction
        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {

            return badRequest(
                    "Invalid sort direction. Use asc or desc"
            );
        }

        // Call ProductService
        // IMPORTANT:
        // Order of parameters matches ProductService
        Page<ProductResponse> products =
                productService.getProductsWithFilters(
                        page,
                        size,
                        sortBy,
                        direction,
                        category,
                        minPrice,
                        maxPrice
                );

        return ResponseEntity.ok(products);
    }

    // =====================================================
    // GET PRODUCT BY ID
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(
            @PathVariable Long id) {

        Optional<ProductResponse> product =
                productService.getProductById(id);

        if (product.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            new ErrorResponse(
                                    404,
                                    "Product not found with id: " + id
                            )
                    );
        }

        return ResponseEntity.ok(product.get());
    }

    // =====================================================
    // CREATE PRODUCT
    // =====================================================

    @PostMapping
    public ResponseEntity<?> createProduct(
            @Valid @RequestBody CreateProductRequest request) {

        ProductResponse createdProduct =
                productService.createProduct(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdProduct);
    }

    // =====================================================
    // UPDATE PRODUCT - PUT
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {

        Optional<ProductResponse> updatedProduct =
                productService.updateProduct(id, request);

        if (updatedProduct.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            new ErrorResponse(
                                    404,
                                    "Product not found with id: " + id
                            )
                    );
        }

        return ResponseEntity.ok(updatedProduct.get());
    }

    // =====================================================
    // PARTIAL UPDATE - PATCH
    // =====================================================

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchProduct(
            @PathVariable Long id,
            @RequestBody PatchProductRequest request) {

        Optional<ProductResponse> updatedProduct =
                productService.patchProduct(id, request);

        if (updatedProduct.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            new ErrorResponse(
                                    404,
                                    "Product not found with id: " + id
                            )
                    );
        }

        return ResponseEntity.ok(updatedProduct.get());
    }

    // =====================================================
    // DELETE PRODUCT
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(
            @PathVariable Long id) {

        boolean deleted =
                productService.deleteProduct(id);

        if (!deleted) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            new ErrorResponse(
                                    404,
                                    "Product not found with id: " + id
                            )
                    );
        }

        return ResponseEntity
                .noContent()
                .build();
    }

    // =====================================================
    // BAD REQUEST
    // =====================================================

    private ResponseEntity<ErrorResponse> badRequest(
            String message) {

        ErrorResponse errorResponse =
                new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        message
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    // =====================================================
    // VALID SORT FIELDS
    // =====================================================

    private boolean isValidSortField(String sortBy) {

        return sortBy.equals("id")
                || sortBy.equals("name")
                || sortBy.equals("price")
                || sortBy.equals("category")
                || sortBy.equals("stock")
                || sortBy.equals("createdAt")
                || sortBy.equals("updatedAt");
    }
}