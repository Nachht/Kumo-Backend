package com.kumo.kumo_backend.repository;

import com.kumo.kumo_backend.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // ===== MÉTODOS PERSONALIZADOS =====

    // Buscar items de un carrito
    List<CartItem> findByCartId(Long cartId);

    // Buscar items de un producto
    List<CartItem> findByProductId(Long productId);

    // Buscar items de un carrito y producto específico
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
}