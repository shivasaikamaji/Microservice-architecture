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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import product_service.dto.CreateProductRequest;
import product_service.dto.ErrorResponse;
import product_service.dto.PatchProductRequest;
import product_service.dto.ProductResponse;
import product_service.dto.UpdateProductRequest;
import product_service.service.ProductService;

@RestController
@RequestMapping("/api/v1/products")
@Tag(
    name = "Products",
    description = "APIs for managing products"
)
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(
        summary = "Get all products",
        description = "Retrieve a paginated list of products with optional filtering and sorting"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Products retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Page.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid pagination, filter, or sorting parameters",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "InvalidPagination",
                    summary = "Invalid pagination parameters",
                    value = """
                        {
                          "status": 400,
                          "message": "Page number cannot be negative",
                          "timestamp": "2026-09-11T12:30:00"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<?> getProducts(

            @Parameter(
                description = "Page number (zero-based)",
                example = "0"
            )
            @RequestParam(defaultValue = "0") int page,

            @Parameter(
                description = "Number of products to return per page",
                example = "10"
            )
            @RequestParam(defaultValue = "10") int size,

            @Parameter(
                description = "Sorting field and direction. Example: id,asc",
                example = "id,asc"
            )
            @RequestParam(defaultValue = "id,asc") String sort,

            @Parameter(
                description = "Filter products by category",
                example = "Electronics"
            )
            @RequestParam(required = false) String category,

            @Parameter(
                description = "Minimum product price",
                example = "100.00"
            )
            @RequestParam(required = false) BigDecimal minPrice,

            @Parameter(
                description = "Maximum product price",
                example = "1000.00"
            )
            @RequestParam(required = false) BigDecimal maxPrice) {

        if (page < 0) {
            return badRequest("Page number cannot be negative");
        }

        if (size <= 0) {
            return badRequest("Page size must be greater than 0");
        }

        if (minPrice != null &&
                minPrice.compareTo(BigDecimal.ZERO) < 0) {
            return badRequest("Minimum price cannot be negative");
        }

        if (maxPrice != null &&
                maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            return badRequest("Maximum price cannot be negative");
        }

        if (minPrice != null &&
                maxPrice != null &&
                minPrice.compareTo(maxPrice) > 0) {
            return badRequest(
                "Minimum price cannot be greater than maximum price"
            );
        }

        String[] sortParts = sort.split(",");

        String sortBy = sortParts[0];
        String direction = "asc";

        if (sortParts.length > 1) {
            direction = sortParts[1];
        }

        if (!isValidSortField(sortBy)) {
            return badRequest(
                "Invalid sort field. Allowed fields: id, name, price, category, stock, createdAt, updatedAt"
            );
        }

        if (!direction.equalsIgnoreCase("asc") &&
                !direction.equalsIgnoreCase("desc")) {
            return badRequest(
                "Invalid sort direction. Use asc or desc"
            );
        }

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

    @GetMapping("/{id}")
    @Operation(
        summary = "Get product by ID",
        description = "Retrieve a product using its unique ID"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Product found successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProductResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "ProductNotFound",
                    summary = "Product does not exist",
                    value = """
                        {
                          "status": 404,
                          "message": "Product not found with id: 101",
                          "timestamp": "2026-09-11T12:30:00"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<?> getProductById(

            @Parameter(
                description = "Unique ID of the product",
                example = "101",
                required = true
            )
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

    @PostMapping
    @Operation(
        summary = "Create a new product",
        description = "Create a new product using the provided product details"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Product created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProductResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid product data",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "InvalidProductData",
                    summary = "Invalid product request",
                    value = """
                        {
                          "status": 400,
                          "message": "Product name is required",
                          "timestamp": "2026-09-11T12:30:00"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<?> createProduct(
            @Valid @RequestBody CreateProductRequest request) {

        ProductResponse createdProduct =
                productService.createProduct(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdProduct);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update a product",
        description = "Replace the details of an existing product"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Product updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProductResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "ProductNotFound",
                    summary = "Product does not exist",
                    value = """
                        {
                          "status": 404,
                          "message": "Product not found with id: 101",
                          "timestamp": "2026-09-11T12:30:00"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<?> updateProduct(

            @Parameter(
                description = "Unique ID of the product to update",
                example = "101",
                required = true
            )
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

    @PatchMapping("/{id}")
    @Operation(
        summary = "Partially update a product",
        description = "Update selected fields of an existing product"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Product updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProductResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "ProductNotFound",
                    summary = "Product does not exist",
                    value = """
                        {
                          "status": 404,
                          "message": "Product not found with id: 101",
                          "timestamp": "2026-09-11T12:30:00"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<?> patchProduct(

            @Parameter(
                description = "Unique ID of the product to update",
                example = "101",
                required = true
            )
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

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a product",
        description = "Delete an existing product using its unique ID"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Product deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "ProductNotFound",
                    summary = "Product does not exist",
                    value = """
                        {
                          "status": 404,
                          "message": "Product not found with id: 101",
                          "timestamp": "2026-09-11T12:30:00"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<?> deleteProduct(

            @Parameter(
                description = "Unique ID of the product to delete",
                example = "101",
                required = true
            )
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

        return ResponseEntity.noContent().build();
    }

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