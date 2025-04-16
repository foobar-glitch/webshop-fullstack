package com.example.webshop.inventoryHandling.ratingTable;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RatingService {

    @Autowired
    private RatingTableRepository ratingTableRepository;


    // Create or update a rating
    public RatingTable saveRating(RatingTable rating) {
        return ratingTableRepository.save(rating);
    }

    // Get all ratings
    public List<RatingTable> getAllRatings() {
        return ratingTableRepository.findAll();
    }

    // Get ratings by inventory ID
    public List<RatingTable> getRatingsByInventoryId(Long inventoryId) {
        return ratingTableRepository.findByInventoryId(inventoryId);
    }

    // Get ratings by user ID
    public List<RatingTable> getRatingsByUserId(Long userId) {
        return ratingTableRepository.findByUserId(userId);
    }

    // Delete a rating
    public void deleteRating(Integer ratingId) {
        ratingTableRepository.deleteById(ratingId);
    }
}