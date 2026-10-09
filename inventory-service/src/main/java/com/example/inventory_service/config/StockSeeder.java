package com.example.inventory_service.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.inventory_service.model.Inventory;
import com.example.inventory_service.service.InventoryService;

@Component
public class StockSeeder implements CommandLineRunner {

    private final InventoryService service;

    public StockSeeder(InventoryService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) {
        for (long productId = 1; productId <= 5; productId++) {
            Inventory inv = new Inventory();
            inv.setProductId(productId);
            inv.setQuantity(100);
            service.add(inv);
        }
        System.out.println("Starting stock added: products 1-5, quantity 100 each");
    }
}