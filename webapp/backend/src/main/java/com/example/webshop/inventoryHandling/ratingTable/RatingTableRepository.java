package com.example.webshop.inventoryHandling.ratingTable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingTableRepository extends JpaRepository<RatingTable, Integer> {

    // Find all ratings for a specific inventory item
    List<RatingTable> findByInventoryId(Long inventoryId);

    // Find all ratings from a specific user
    List<RatingTable> findByUserId(Long userId);

    List<RatingTable> findByUserIdAndInventoryId(Long userId, Long inventoryId);

}