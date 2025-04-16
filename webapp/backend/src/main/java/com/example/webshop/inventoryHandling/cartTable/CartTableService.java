package com.example.webshop.inventoryHandling.cartTable;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartTableService {

    @Autowired
    private CartTableRepository cartTableRepository;


    // Get all cart items
    public List<CartTable> getAllCarts() {
        return cartTableRepository.findAll();
    }

    // Get cart items by user ID
    public List<CartTable> getCartsByUserId(Long userId) {
        return cartTableRepository.findByUserId(userId);
    }

    // Get a single cart item by ID
    public Optional<CartTable> getCartById(Integer cartId) {
        return cartTableRepository.findById(cartId);
    }

    public List<CartTable> getCartsByUserIdAndInventoryId(Long userId, Long inventoryId){
        return cartTableRepository.findByUserIdAndInventoryId(userId, inventoryId);
    }

    // Add or update a cart item
    public CartTable saveCart(CartTable cart) {
        return cartTableRepository.save(cart);
    }

    // Delete a cart item by ID
    public void deleteCartById(Integer cartId) {
        cartTableRepository.deleteById(cartId);
    }

    // Optionally: delete all cart items for a user
    public void deleteCartsByUserId(Long userId) {
        List<CartTable> userCarts = cartTableRepository.findByUserId(userId);
        cartTableRepository.deleteAll(userCarts);
    }
}

