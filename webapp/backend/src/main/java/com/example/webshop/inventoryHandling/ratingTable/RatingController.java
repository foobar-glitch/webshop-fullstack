package com.example.webshop.inventoryHandling.ratingTable;

import com.example.webshop.userAuthentication.users.User;
import com.example.webshop.userAuthentication.users.UserService;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
public class RatingController {

    @Autowired
    private RatingTableRepository ratingTableRepository;

    @Autowired
    private UserService userService;


    // POST /rating - Submit a new rating
    @PostMapping("/rating")
    public ResponseEntity<RatingTable> submitRating(@RequestParam Long inventory_id,
                                    @RequestParam Long stars,
                                    @RequestParam(required = false) String comment, HttpServletRequest request) {
        User user = userService.getUser(request);
        if(user==null){
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        // Delete all comments by that user and inventory_id to not have duplicate comments
        for(RatingTable o: ratingTableRepository.findByUserIdAndInventoryId(user.getUserId(), inventory_id)){
            ratingTableRepository.delete(o);
        }

        RatingTable rating = new RatingTable();
        rating.setInventoryId(inventory_id);
        rating.setStars(stars);
        rating.setComment(comment);
        rating.setUserId(user.getUserId()); // Normally you’d get this from session/auth
        rating.setCreatedAt(LocalDateTime.now());
        rating.setUpdatedAt(LocalDateTime.now());

        return ResponseEntity.ok(ratingTableRepository.save(rating));
    }

    // GET /rating?inventoryId=3 - Get all ratings for a product
    @GetMapping("/rating")
    public ResponseEntity<List<RatingTable>> getRatings(@RequestParam(required = false) Long inventoryId,
                                        @RequestParam(required = false) Long userId) {
        List<RatingTable> rating;
        if (inventoryId != null) {
            rating = ratingTableRepository.findByInventoryId(inventoryId);
        } else if (userId != null) {
            rating = ratingTableRepository.findByUserId(userId);
        } else {
            rating = new ArrayList<>();
        }
        rating.sort(Comparator.comparing(RatingTable::getCreatedAt).reversed());
        return ResponseEntity.ok(rating);
    }

    // average-rating?inventoryId=?
    @GetMapping("/average-rating")
    public ResponseEntity<Float> averageRating(@RequestParam Long inventoryId){
        List<RatingTable> ratingTables = ratingTableRepository.findByInventoryId(inventoryId);
        if(ratingTables.isEmpty()){
            return ResponseEntity.ok(0.0f);
        }
        double stars = 0f;
        for(RatingTable r : ratingTables){
            stars += r.getStars();
        }
        return ResponseEntity.ok((float) stars/ratingTables.size());
    }

    // number-rating?inventoryId=?
    @GetMapping("/number-rating")
    public ResponseEntity<Integer> numberRating(@RequestParam Long inventoryId){
        List<RatingTable> ratingTables = ratingTableRepository.findByInventoryId(inventoryId);
        if(ratingTables.isEmpty()){
            return ResponseEntity.ok(0);
        }
        return ResponseEntity.ok(ratingTables.size());
    }
        
}