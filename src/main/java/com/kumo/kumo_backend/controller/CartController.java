package com.kumo.kumo_backend.controller;

import com.kumo.kumo_backend.dto.AddToCartRequestDTO;
import com.kumo.kumo_backend.dto.CartDTO;
import com.kumo.kumo_backend.model.Cart;
import com.kumo.kumo_backend.model.User;
import com.kumo.kumo_backend.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.math.BigDecimal;


@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // ===== OBTENER CARRITO DEL USUARIO LOGUEADO =====
    @GetMapping
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<CartDTO> getActiveCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        System.out.println("🔍 Obteniendo carrito para userId: " + userId);
        Cart cart = cartService.findActiveCartByUser(userId);
        return ResponseEntity.ok(new CartDTO(cart));
    }

    // ===== OBTENER TOTAL DEL CARRITO =====
    @GetMapping("/total")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<BigDecimal> getCartTotal(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        return ResponseEntity.ok(cartService.getCartTotal(userId));
    }

    // ===== AGREGAR PRODUCTO AL CARRITO =====
    @PostMapping("/add")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<CartDTO> addProductToCart(
            @RequestBody AddToCartRequestDTO request,
            Authentication authentication) {
        try {
            System.out.println("🔍 ===== RECIBIENDO PETICIÓN /add =====");
            System.out.println("🔍 Request: " + request);

            // 🔥 OBTENER EL ID DEL USUARIO DEL TOKEN
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();

            System.out.println("🔍 User ID del token: " + userId);
            System.out.println("🔍 User nombre: " + user.getNombre());
            System.out.println("🔍 User email: " + user.getEmail());
            System.out.println("🔍 ProductId del request: " + request.getProductId());
            System.out.println("🔍 Cantidad del request: " + request.getCantidad());

            if (request.getProductId() == null) {
                return ResponseEntity.badRequest().build();
            }

            Integer cantidad = request.getCantidad() != null ? request.getCantidad() : 1;
            Cart cart = cartService.addProductToCart(userId, request.getProductId(), cantidad);
            return ResponseEntity.status(HttpStatus.CREATED).body(new CartDTO(cart));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ===== ELIMINAR PRODUCTO DEL CARRITO =====
    @DeleteMapping("/remove/{productId}")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<CartDTO> removeProductFromCart(
            @PathVariable Long productId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        Cart cart = cartService.removeProductFromCart(userId, productId);
        return ResponseEntity.ok(new CartDTO(cart));
    }

    // ===== ACTUALIZAR CANTIDAD =====
    @PutMapping("/update")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<CartDTO> updateCartItemQuantity(
            @RequestBody AddToCartRequestDTO request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        Cart cart = cartService.updateCartItemQuantity(
                userId,
                request.getProductId(),
                request.getCantidad()
        );
        return ResponseEntity.ok(new CartDTO(cart));
    }

    // ===== VACIAR CARRITO =====
    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<CartDTO> clearCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        Cart cart = cartService.clearCart(userId);
        return ResponseEntity.ok(new CartDTO(cart));
    }

    // ===== CREAR CARRITO (para compatibilidad) =====
    @PostMapping("/create")
    public ResponseEntity<Cart> createCart(@RequestParam Long userId) {
        return ResponseEntity.ok(cartService.createCartForUser(userId));
    }
}