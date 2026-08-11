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
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, UserRepository userRepository,
                       ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public Cart findById(Long id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public Cart findActiveCartByUser(Long userId) {
        System.out.println("🔍 findActiveCartByUser - userId: " + userId);

        if (userId == null) {
            throw new RuntimeException("El userId no puede ser null");
        }

        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + userId);
        }

        return cartRepository.findByUserIdAndEstado(userId, "activo")
                .orElseGet(() -> createCartForUser(userId));
    }

    @Transactional
    public Cart createCartForUser(Long userId) {
        System.out.println("🔍 createCartForUser - userId: " + userId);

        if (userId == null) {
            throw new RuntimeException("El userId no puede ser null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setUsuarioId(userId); // 🔥 ESTABLECER EL ID DEL USUARIO
        cart.setEstado("activo");
        cart.setFechaCreacion(LocalDateTime.now());

        System.out.println("🔍 Creando carrito para userId: " + userId);

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart addProductToCart(Long userId, Long productId, Integer quantity) {
        System.out.println("🔍 addProductToCart - userId: " + userId);
        System.out.println("🔍 addProductToCart - productId: " + productId);
        System.out.println("🔍 addProductToCart - quantity: " + quantity);

        if (userId == null) {
            throw new RuntimeException("El userId no puede ser null");
        }

        Cart cart = findActiveCartByUser(userId);

        // Verificar si el producto ya existe en el carrito
        boolean productoExistente = false;
        for (CartItem item : cart.getCartItems()) {
            if (item.getProduct().getId().equals(productId)) {
                // Sumar cantidad
                int nuevaCantidad = item.getCantidad() + quantity;
                item.setCantidad(nuevaCantidad);
                productoExistente = true;
                System.out.println("✅ Producto existente, nueva cantidad: " + nuevaCantidad);
                break;
            }
        }

        // Si no existe, agregar nuevo
        if (!productoExistente) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productId));

            if (product.getStock() < quantity) {
                throw new RuntimeException("Stock insuficiente. Disponible: " + product.getStock());
            }

            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setCantidad(quantity);
            newItem.setPrecioUnitario(product.getPrecio());
            newItem.setCart(cart);
            cart.getCartItems().add(newItem);
            System.out.println("✅ Nuevo producto agregado al carrito");
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeProductFromCart(Long userId, Long productId) {
        Cart cart = findActiveCartByUser(userId);

        boolean removed = cart.getCartItems().removeIf(item -> item.getProduct().getId().equals(productId));

        if (!removed) {
            throw new RuntimeException("Producto no encontrado en el carrito");
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateCartItemQuantity(Long userId, Long productId, Integer quantity) {
        if (quantity < 0) {
            throw new RuntimeException("La cantidad no puede ser negativa");
        }

        Cart cart = findActiveCartByUser(userId);

        if (quantity == 0) {
            cart.getCartItems().removeIf(item -> item.getProduct().getId().equals(productId));
        } else {
            boolean found = false;
            for (CartItem item : cart.getCartItems()) {
                if (item.getProduct().getId().equals(productId)) {
                    item.setCantidad(quantity);
                    found = true;
                    break;
                }
            }

            if (!found) {
                throw new RuntimeException("Producto no encontrado en el carrito");
            }
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart clearCart(Long userId) {
        Cart cart = findActiveCartByUser(userId);
        cart.getCartItems().clear();
        return cartRepository.save(cart);
    }

    @Transactional(readOnly = true)
    public BigDecimal getCartTotal(Long userId) {
        Cart cart = findActiveCartByUser(userId);
        return cart.getCartItems().stream()
                .map(item -> item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public Cart save(Cart cart) {
        return cartRepository.save(cart);
    }

    @Transactional(readOnly = true)
    public List<Cart> findCartsByUser(Long userId) {
        return cartRepository.findByUserId(userId);
    }
}