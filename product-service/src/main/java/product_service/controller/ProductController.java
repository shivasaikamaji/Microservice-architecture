package product_service.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import product_service.entity.Product;
import product_service.repository.ProductRepository;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;

    // Constructor
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // =========================
    // CREATE PRODUCT
    // =========================
    @PostMapping
    public Product createProduct(@RequestBody @NonNull Product product) {
        return productRepository.save(product);
    }

    // =========================
    // GET ALL PRODUCTS
    // =========================
    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // =========================
    // GET PRODUCT BY ID
    // =========================
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable @NonNull Long id) {

        Optional<Product> optionalProduct =
                productRepository.findById(id);

        if (optionalProduct.isPresent()) {
            return optionalProduct.get();
        }

        return null;
    }

    // =========================
    // UPDATE PRODUCT
    // =========================
    @PutMapping("/{id}")
    public Product updateProduct(
            @PathVariable @NonNull Long id,
            @RequestBody Product productDetails) {

        Optional<Product> optionalProduct =
                productRepository.findById(id);

        if (optionalProduct.isPresent()) {

            Product product = optionalProduct.get();

            product.setName(productDetails.getName());
            product.setPrice(productDetails.getPrice());

            return productRepository.save(product);
        }

        return null;
    }

    // =========================
    // DELETE PRODUCT
    // =========================
    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable @NonNull Long id) {

        if (productRepository.existsById(id)) {

            productRepository.deleteById(id);

            return "Product deleted successfully";
        }

        return "Product not found";
    }
}