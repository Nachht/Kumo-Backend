package com.kumo.kumo_backend.service;

import com.kumo.kumo_backend.model.Cart;
import com.kumo.kumo_backend.model.CartItem;
import com.kumo.kumo_backend.model.Product;
import com.kumo.kumo_backend.model.User;
import com.kumo.kumo_backend.repository.CartRepository;
import com.kumo.kumo_backend.repository.ProductRepository;
import com.kumo.kumo_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // ===== BUSCAR CARRO POR ID =====
    @Transactional(readOnly = true)
    public Cart findById(Long id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado con ID: " + id));
    }

    // ===== BUSCAR CARRO ACTIVO DE UN USUARIO =====
    @Transactional(readOnly = true)
    public Cart findActiveCartByUser(Long userId) {
        // Verificar que el usuario existe
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + userId);
        }

        return cartRepository.findByUserIdAndEstado(userId, "activo")
                .orElseGet(() -> createCartForUser(userId));
    }

    // ===== CREAR CARRO PARA USUARIO =====
    @Transactional
    public Cart createCartForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        Cart cart = new Cart(user);
        return cartRepository.save(cart);
    }

    // ===== AGREGAR PRODUCTO AL CARRO =====
    @Transactional
    public Cart addProductToCart(Long userId, Long productId, Integer quantity) {
        // 1. Obtener el carrito activo del usuario
        Cart cart = findActiveCartByUser(userId);

        // 2. Verificar que el producto existe y tiene stock
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productId));

        if (product.getStock() < quantity) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + product.getStock());
        }

        // 3. Buscar si el producto ya está en el carrito
        CartItem existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Si ya existe, actualizar cantidad
            existingItem.setCantidad(existingItem.getCantidad() + quantity);
        } else {
            // Si no existe, crear nuevo item
            CartItem newItem = new CartItem();
            newItem.setCantidad(quantity);
            newItem.setPrecioUnitario(product.getPrecio());
            newItem.setProduct(product);
            cart.addCartItem(newItem);
        }

        return cartRepository.save(cart);
    }

    // ===== ELIMINAR PRODUCTO DEL CARRO =====
    @Transactional
    public Cart removeProductFromCart(Long userId, Long productId) {
        Cart cart = findActiveCartByUser(userId);

        CartItem itemToRemove = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en el carrito"));

        cart.removeCartItem(itemToRemove);
        return cartRepository.save(cart);
    }

    // ===== ACTUALIZAR CANTIDAD DE PRODUCTO EN EL CARRO =====
    @Transactional
    public Cart updateCartItemQuantity(Long userId, Long productId, Integer quantity) {
        if (quantity <= 0) {
            return removeProductFromCart(userId, productId);
        }

        Cart cart = findActiveCartByUser(userId);

        CartItem item = cart.getCartItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en el carrito"));

        // Validar stock
        Product product = item.getProduct();
        if (product.getStock() < quantity) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + product.getStock());
        }

        item.setCantidad(quantity);
        return cartRepository.save(cart);
    }

    // ===== VACIAR CARRO =====
    @Transactional
    public Cart clearCart(Long userId) {
        Cart cart = findActiveCartByUser(userId);
        cart.getCartItems().clear();
        return cartRepository.save(cart);
    }

    // ===== OBTENER TOTAL DEL CARRO =====
    @Transactional(readOnly = true)
    public BigDecimal getCartTotal(Long userId) {
        Cart cart = findActiveCartByUser(userId);
        return cart.getTotal();
    }

    // ===== BUSCAR CARROS DE UN USUARIO =====
    @Transactional(readOnly = true)
    public List<Cart> findCartsByUser(Long userId) {
        return cartRepository.findByUserId(userId);
    }
}