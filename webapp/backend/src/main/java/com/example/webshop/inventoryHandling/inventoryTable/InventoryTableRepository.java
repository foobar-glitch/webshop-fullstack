package com.example.webshop.inventoryHandling.inventoryTable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryTableRepository extends JpaRepository<InventoryTable, Integer> {
    // You can define custom query methods here if needed

    // For example, a method to find inventory by category
    List<InventoryTable> findByCategory(String category);

    // You can also define more queries like:
    List<InventoryTable> findByProductNameContaining(String productName);
}