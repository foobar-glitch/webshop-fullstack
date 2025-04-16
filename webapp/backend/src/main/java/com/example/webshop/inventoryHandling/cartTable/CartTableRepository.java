package com.example.webshop.inventoryHandling.cartTable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartTableRepository extends JpaRepository<CartTable, Integer> {
    // Custom query methods can go here, for example:

    // Find all cart items by user ID
    List<CartTable> findByUserId(Long userId);

    // Find a cart item by user ID and inventory ID (if you want to prevent duplicates)
    List<CartTable> findByUserIdAndInventoryId(Long userId, Long inventoryId);
}