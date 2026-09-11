package product_service.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for partially updating an existing product")
public class PatchProductRequest {

    @Size(min = 1, message = "Product name cannot be empty")
    @Schema(
        description = "Updated name of the product",
        example = "Laptop Pro",
        minLength = 1
    )
    private String name;

    @Schema(
        description = "Updated description of the product",
        example = "15-inch professional laptop with 32GB RAM"
    )
    private String description;

    @Positive(message = "Product price must be greater than 0")
    @Schema(
        description = "Updated price of the product",
        example = "1299.99",
        minimum = "0"
    )
    private BigDecimal price;

    @Schema(
        description = "Updated category of the product",
        example = "Electronics"
    )
    private String category;

    @PositiveOrZero(message = "Product stock cannot be negative")
    @Schema(
        description = "Updated number of units available in stock",
        example = "15",
        minimum = "0"
    )
    private Integer stock;

    @Schema(
        description = "Updated status of the product",
        example = "ACTIVE"
    )
    private String status;

    public PatchProductRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}