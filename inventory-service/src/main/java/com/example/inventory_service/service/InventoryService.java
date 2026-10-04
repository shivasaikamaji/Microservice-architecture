package com.example.inventory_service.service;

import com.example.inventory_service.model.*;
import com.example.inventory_service.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class InventoryService {
    private final InventoryRepository repo;
    private final ReservationRepository resRepo;

    public InventoryService(InventoryRepository repo, ReservationRepository resRepo) {
        this.repo = repo; this.resRepo = resRepo;
    }

    public Inventory add(Inventory inv) {
        return repo.findByProductId(inv.getProductId())
            .map(e -> { e.setQuantity(inv.getQuantity()); return repo.save(e); })
            .orElseGet(() -> repo.save(inv));
    }

    public List<Inventory> getAll() { return repo.findAll(); }

    public Inventory getByProductId(Long productId) {
        return repo.findByProductId(productId)
            .orElseThrow(() -> new RuntimeException("Product not in inventory"));
    }

    public Inventory updateStock(Long productId, int quantity) {
        Inventory inv = getByProductId(productId);
        inv.setQuantity(quantity);
        return repo.save(inv);
    }

    @Transactional
    public boolean reserve(Long orderId, Long productId, int qty) {
        if (resRepo.existsById(orderId)) return true;
        Inventory inv = repo.findByProductId(productId).orElse(null);
        if (inv == null || inv.getQuantity() < qty) return false;
        inv.setQuantity(inv.getQuantity() - qty);
        inv.setReservedQuantity(inv.getReservedQuantity() + qty);
        repo.save(inv);
        resRepo.save(new Reservation(orderId, productId, qty));
        return true;
    }

    @Transactional
    public boolean release(Long orderId) {
        Reservation r = resRepo.findById(orderId).orElse(null);
        if (r == null || !r.getStatus().equals("RESERVED")) return false;
        Inventory inv = getByProductId(r.getProductId());
        inv.setQuantity(inv.getQuantity() + r.getQuantity());
        inv.setReservedQuantity(inv.getReservedQuantity() - r.getQuantity());
        repo.save(inv);
        r.setStatus("RELEASED");
        resRepo.save(r);
        return true;
    }

    @Transactional
    public void complete(Long orderId) {
        Reservation r = resRepo.findById(orderId).orElse(null);
        if (r == null || !r.getStatus().equals("RESERVED")) return;
        Inventory inv = getByProductId(r.getProductId());
        inv.setReservedQuantity(inv.getReservedQuantity() - r.getQuantity());
        repo.save(inv);
        r.setStatus("COMPLETED");
        resRepo.save(r);
    }
}