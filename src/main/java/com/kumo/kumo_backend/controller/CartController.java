package com.kumo.kumo_backend.controller;

import com.kumo.kumo_backend.model.Cart;
import com.kumo.kumo_backend.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // ===== GET /api/cart/{userId} =====
    // Obtener el carrito activo de un usuario
    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getActiveCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.findActiveCartByUser(userId));
    }

    // ===== GET /api/cart/{userId}/total =====
    // Obtener el total del carrito
    @GetMapping("/{userId}/total")
    public ResponseEntity<BigDecimal> getCartTotal(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartTotal(userId));
    }

    // ===== POST /api/cart/{userId}/add =====
    // Agregar producto al carrito
    @PostMapping("/{userId}/add")
    public ResponseEntity<Cart> addProductToCart(
            @PathVariable Long userId,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        return ResponseEntity.ok(cartService.addProductToCart(userId, productId, quantity));
    }

    // ===== DELETE /api/cart/{userId}/remove =====
    // Eliminar producto del carrito
    @DeleteMapping("/{userId}/remove")
    public ResponseEntity<Cart> removeProductFromCart(
            @PathVariable Long userId,
            @RequestParam Long productId) {
        return ResponseEntity.ok(cartService.removeProductFromCart(userId, productId));
    }

    // ===== PUT /api/cart/{userId}/update =====
    // Actualizar cantidad de un producto en el carrito
    @PutMapping("/{userId}/update")
    public ResponseEntity<Cart> updateCartItemQuantity(
            @PathVariable Long userId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.updateCartItemQuantity(userId, productId, quantity));
    }

    // ===== DELETE /api/cart/{userId}/clear =====
    // Vaciar carrito
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Cart> clearCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.clearCart(userId));
    }
}