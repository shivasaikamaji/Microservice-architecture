package com.example.inventory_service.controller;

import com.example.inventory_service.model.Inventory;
import com.example.inventory_service.service.InventoryService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    private final InventoryService service;

    public InventoryController(InventoryService service) { this.service = service; }

    @PostMapping
    public Inventory add(@RequestBody Inventory inv) { return service.add(inv); }

    @GetMapping
    public List<Inventory> getAll() { return service.getAll(); }

    @GetMapping("/{productId}")
    public Inventory get(@PathVariable Long productId) { return service.getByProductId(productId); }

    @PutMapping("/{productId}")
    public Inventory update(@PathVariable Long productId, @RequestParam int quantity) {
        return service.updateStock(productId, quantity);
    }
}