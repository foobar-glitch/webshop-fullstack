package com.example.webshop.inventoryHandling.cartTable;


import com.example.webshop.inventoryHandling.inventoryTable.InventoryTable;
import com.example.webshop.inventoryHandling.inventoryTable.InventoryTableRepository;
import com.example.webshop.inventoryHandling.inventoryTable.InventoryTableService;
import com.example.webshop.userAuthentication.users.User;
import com.example.webshop.userAuthentication.users.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
public class CartTableController {
    @Autowired
    private CartTableService cartTableService;

    @Autowired
    private InventoryTableService inventoryTableService;

    @Autowired
    private UserService userService;

    @PostMapping("/cart")
    public ResponseEntity<String> addToCart(@RequestParam Long inventoryId, @RequestParam int quantity,
                                            HttpServletRequest request){
        User user = userService.getUser(request);
        if(user == null){
            return new ResponseEntity<>("Not logged in", HttpStatus.UNAUTHORIZED);
        }
        for(CartTable o: cartTableService.getCartsByUserIdAndInventoryId(user.getUserId(), inventoryId)){
            cartTableService.deleteCartById(Math.toIntExact(o.getCartId()));
        }

        CartTable cartTable = new CartTable();
        cartTable.setUserId(user.getUserId());
        cartTable.setQuantity(quantity);
        cartTable.setInventoryId(inventoryId);
        cartTable.setCreatedAt(LocalDateTime.now());
        cartTable.setUpdatedAt(LocalDateTime.now());
        cartTableService.saveCart(cartTable);
        return new ResponseEntity<>("Created Entry", HttpStatus.OK);

    }


    @GetMapping("/cart")
    public ResponseEntity<List<CartTable>> showCartOfUser(HttpServletRequest request){
        User user = userService.getUser(request);
        if(user == null){
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(cartTableService.getCartsByUserId(user.getUserId()));
    }

    @DeleteMapping("/cart")
    public ResponseEntity<String> deleteEntry(HttpServletRequest request, Integer cartId){
        User user = userService.getUser(request);
        if(user == null){
            return new ResponseEntity<>("Not logged in", HttpStatus.UNAUTHORIZED);
        }
        cartTableService.deleteCartById(cartId);
        return new ResponseEntity<>("Deleting worked", HttpStatus.OK);
    }

    @GetMapping("/cart/cost")
    public ResponseEntity<BigDecimal> calculateCost(HttpServletRequest request){
        User user = userService.getUser(request);
        BigDecimal cost = new BigDecimal("0.0");
        if(user == null){
            return new ResponseEntity<>(cost, HttpStatus.UNAUTHORIZED);
        }

        for(CartTable cart: cartTableService.getCartsByUserId(user.getUserId())){
            Optional<InventoryTable> optionalInventoryTable = inventoryTableService.getInventoryById(
                    Math.toIntExact(cart.getInventoryId()));
            if(optionalInventoryTable.isPresent()){
               cost=cost.add(optionalInventoryTable.get().getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
            }
        }
        return ResponseEntity.ok(cost);

    }


}
