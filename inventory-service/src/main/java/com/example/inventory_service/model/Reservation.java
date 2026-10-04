package com.example.inventory_service.model;

import jakarta.persistence.*;

@Entity
public class Reservation {
    @Id
    private Long orderId;
    private Long productId;
    private int quantity;
    private String status; // RESERVED, RELEASED, COMPLETED

    public Reservation() {}
    public Reservation(Long orderId, Long productId, int quantity) {
        this.orderId = orderId; this.productId = productId;
        this.quantity = quantity; this.status = "RESERVED";
    }
    public Long getOrderId() { return orderId; }
    public Long getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}