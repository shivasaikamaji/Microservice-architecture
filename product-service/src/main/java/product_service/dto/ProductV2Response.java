package product_service.dto;

import java.math.BigDecimal;

public class ProductV2Response {

    private Long id;
    private String name;
    private BigDecimal price;
    private String category;
    private Integer stock;
    private String status;

    public ProductV2Response() {
    }

    public ProductV2Response(
            Long id,
            String name,
            BigDecimal price,
            String category,
            Integer stock,
            String status) {

        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.stock = stock;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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