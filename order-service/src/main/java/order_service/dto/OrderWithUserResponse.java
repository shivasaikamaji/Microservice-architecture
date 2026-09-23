package order_service.dto;

import java.math.BigDecimal;

public class OrderWithUserResponse {

    private Long orderId;
    private String productName;
    private Integer quantity;
    private BigDecimal amount;
    private String status;
    private UserResponse user;
    private String message; // Step 6: set only when User Service is unavailable (fallback triggered)

    public OrderWithUserResponse() {
    }

    public OrderWithUserResponse(Long orderId, String productName, Integer quantity,
                                  BigDecimal amount, String status, UserResponse user) {
        this.orderId = orderId;
        this.productName = productName;
        this.quantity = quantity;
        this.amount = amount;
        this.status = status;
        this.user = user;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}