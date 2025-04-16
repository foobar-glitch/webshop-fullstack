package com.example.webshop.inventoryHandling.inventoryTable;

import com.example.webshop.userAuthentication.users.User;
import com.example.webshop.userAuthentication.users.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryTableService inventoryTableService;

    @Autowired
    private UserService userService;


    // Create or Update an inventory item
    @PostMapping
    public ResponseEntity<InventoryTable> createOrUpdateInventory(
            @RequestParam String productName, @RequestParam String description, @RequestParam BigDecimal price,
            @RequestParam int quantity, @RequestParam String category, HttpServletRequest request) {
        User user = userService.getUser(request);
        if(user==null || !user.isAdmin()){
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        InventoryTable inventory = new InventoryTable();
        inventory.setProductName(productName);
        inventory.setDescription(description);
        inventory.setPrice(price);
        inventory.setQuantity(quantity);
        inventory.setCategory(category);
        inventory.setCreatedAt(LocalDateTime.now());
        inventory.setUpdatedAt(LocalDateTime.now());

        InventoryTable savedInventory = inventoryTableService.saveInventory(inventory);
        return ResponseEntity.ok(savedInventory);
    }

    // Get all inventory items
    @GetMapping
    public ResponseEntity<List<InventoryTable>> getAllInventory(@RequestParam(required = false) String category) {
        if(category == null) {
            return ResponseEntity.ok(inventoryTableService.getAllInventory()); // Returns a list of inventory items
        }
        return ResponseEntity.ok(inventoryTableService.getInventoryByCategory(category));
    }

    // Get a specific inventory item by ID
    @GetMapping("/{id}")
    public ResponseEntity<InventoryTable> getInventoryById(@PathVariable int id, HttpServletRequest request) {
        Optional<InventoryTable> inventory = inventoryTableService.getInventoryById(id);
        return inventory
                .map(ResponseEntity::ok)  // Return the found inventory item
                .orElseGet(() -> ResponseEntity.notFound().build());  // Return HTTP 404 if not found
    }


    // Delete inventory item by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable int id, HttpServletRequest request) {
        User user = userService.getUser(request);
        if(user==null || !user.isAdmin()){
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        if (inventoryTableService.deleteInventory(id)) {
            return ResponseEntity.noContent().build(); // HTTP 204 No Content if the delete was successful
        }
        return ResponseEntity.notFound().build(); // HTTP 404 Not Found if the item doesn't exist
    }

    // Update an existing inventory item
    @PutMapping("/{id}")
    public ResponseEntity<InventoryTable> updateInventory(@PathVariable int id,
                                                          @RequestBody InventoryTable updatedInventory,
                                                          HttpServletRequest request) {
        User user = userService.getUser(request);
        if(user==null || !user.isAdmin()){
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        Optional<InventoryTable> inventory = inventoryTableService.updateInventory(id, updatedInventory);
        return inventory
                .map(ResponseEntity::ok)  // Return the updated inventory item
                .orElseGet(() -> ResponseEntity.notFound().build());  // Return HTTP 404 if the item was not found
    }
}