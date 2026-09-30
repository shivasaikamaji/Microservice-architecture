package order_service.dto;

import java.math.BigDecimal;

public class OrderEvent {

    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private String eventType;

    public OrderEvent() {
    }

    public OrderEvent(Long orderId, Long userId, BigDecimal amount, String eventType) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.eventType = eventType;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}