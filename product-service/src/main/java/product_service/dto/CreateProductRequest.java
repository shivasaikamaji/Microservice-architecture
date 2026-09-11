package product_service.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for creating a new product")
public class CreateProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    @Schema(
        description = "Name of the product",
        example = "Laptop",
        minLength = 2,
        maxLength = 100,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(
        description = "Detailed description of the product",
        example = "15-inch business laptop with 16GB RAM",
        maxLength = 500
    )
    private String description;

    @NotNull(message = "Product price is required")
    @PositiveOrZero(message = "Product price must be greater than or equal to 0")
    @Schema(
        description = "Price of the product",
        example = "799.99",
        minimum = "0",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BigDecimal price;

    @NotBlank(message = "Product category is required")
    @Size(min = 2, max = 50, message = "Product category must be between 2 and 50 characters")
    @Schema(
        description = "Category of the product",
        example = "Electronics",
        minLength = 2,
        maxLength = 50,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String category;

    @NotNull(message = "Product stock is required")
    @PositiveOrZero(message = "Product stock must be greater than or equal to 0")
    @Schema(
        description = "Number of units currently available",
        example = "25",
        minimum = "0",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer stock;

    public CreateProductRequest() {
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
}