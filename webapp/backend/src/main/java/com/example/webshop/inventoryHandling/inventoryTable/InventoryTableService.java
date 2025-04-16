package com.example.webshop.inventoryHandling.inventoryTable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryTableService {

    @Autowired
    private InventoryTableRepository inventoryTableRepository;


    // Create or Update inventory item
    public InventoryTable saveInventory(InventoryTable inventory) {
        return inventoryTableRepository.save(inventory);
    }

    // Get all inventory items
    public List<InventoryTable> getAllInventory() {
        return inventoryTableRepository.findAll();
    }

    // Get inventory item by ID
    public Optional<InventoryTable> getInventoryById(Integer inventoryId) {
        return inventoryTableRepository.findById(inventoryId);
    }

    // Get inventory items by category
    public List<InventoryTable> getInventoryByCategory(String category) {
        return inventoryTableRepository.findByCategory(category);
    }

    // Delete inventory item by ID
    public boolean deleteInventory(int inventoryId) {
        if (inventoryTableRepository.existsById(inventoryId)) {
            inventoryTableRepository.deleteById(inventoryId);
            return true;
        }
        return false;
    }

    // Update inventory item (can be used for partial updates if needed)
    public Optional<InventoryTable> updateInventory(int inventoryId, InventoryTable updatedInventory) {
        Optional<InventoryTable> existingInventory = inventoryTableRepository.findById(inventoryId);
        if (existingInventory.isPresent()) {
            InventoryTable inventoryToUpdate = existingInventory.get();
            inventoryToUpdate.setProductName(updatedInventory.getProductName());
            inventoryToUpdate.setDescription(updatedInventory.getDescription());
            inventoryToUpdate.setPrice(updatedInventory.getPrice());
            inventoryToUpdate.setQuantity(updatedInventory.getQuantity());
            inventoryToUpdate.setCategory(updatedInventory.getCategory());
            inventoryToUpdate.setUpdatedAt(updatedInventory.getUpdatedAt());
            return Optional.of(inventoryTableRepository.save(inventoryToUpdate));
        }
        return Optional.empty();
    }
}