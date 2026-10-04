package com.example.payment_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.payment_service.entity.Payment;
import com.example.payment_service.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // Process a payment for a given order
    @Transactional
    public Payment processPayment(Long orderId, BigDecimal amount) {

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setCreatedAt(LocalDateTime.now());

        // Simple simulation: payments over 100000 "fail", everything else succeeds.
        // This lets us test both the success and failure paths of the saga later.
        if (amount.compareTo(new BigDecimal("100000")) > 0) {
            payment.setPaymentStatus("FAILED");
        } else {
            payment.setPaymentStatus("SUCCESS");
            payment.setTransactionReference("TXN-" + System.currentTimeMillis());
        }

        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }
}