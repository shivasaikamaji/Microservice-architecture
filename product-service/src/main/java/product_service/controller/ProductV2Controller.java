package product_service.controller;

import java.util.List;
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

import jakarta.validation.Valid;
import product_service.dto.CreateProductV2Request;
import product_service.dto.ErrorResponse;
import product_service.dto.PatchProductV2Request;
import product_service.dto.ProductV2Response;
import product_service.dto.UpdateProductV2Request;
import product_service.service.ProductV2Service;

@RestController
@RequestMapping("/api/v2/products")
public class ProductV2Controller {

    private final ProductV2Service productV2Service;

    public ProductV2Controller(
            ProductV2Service productV2Service) {

        this.productV2Service = productV2Service;
    }

    // GET ALL PRODUCTS
    @GetMapping
    public ResponseEntity<List<ProductV2Response>> getProductsV2() {

        List<ProductV2Response> products =
                productV2Service.getAllProducts();

        return ResponseEntity.ok(products);
    }

    // GET PRODUCT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(
            @PathVariable Long id) {

        Optional<ProductV2Response> product =
                productV2Service.getProductById(id);

        if (product.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(
                            404,
                            "Product not found with id: " + id
                    ));
        }

        return ResponseEntity.ok(product.get());
    }

    // CREATE PRODUCT
    @PostMapping
    public ResponseEntity<ProductV2Response> createProduct(
            @Valid @RequestBody CreateProductV2Request request) {

        ProductV2Response createdProduct =
                productV2Service.createProduct(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdProduct);
    }

    // FULL UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductV2Request request) {

        Optional<ProductV2Response> updatedProduct =
                productV2Service.updateProduct(id, request);

        if (updatedProduct.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(
                            404,
                            "Product not found with id: " + id
                    ));
        }

        return ResponseEntity.ok(updatedProduct.get());
    }

    // PARTIAL UPDATE
    @PatchMapping("/{id}")
    public ResponseEntity<?> patchProduct(
            @PathVariable Long id,
            @RequestBody PatchProductV2Request request) {

        Optional<ProductV2Response> updatedProduct =
                productV2Service.patchProduct(id, request);

        if (updatedProduct.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(
                            404,
                            "Product not found with id: " + id
                    ));
        }

        return ResponseEntity.ok(updatedProduct.get());
    }

    // DELETE PRODUCT
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(
            @PathVariable Long id) {

        boolean deleted =
                productV2Service.deleteProduct(id);

        if (!deleted) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(
                            404,
                            "Product not found with id: " + id
                    ));
        }

        return ResponseEntity.noContent().build();
    }
}