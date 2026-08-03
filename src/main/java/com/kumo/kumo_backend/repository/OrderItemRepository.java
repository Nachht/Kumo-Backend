package com.kumo.kumo_backend.repository;

import com.kumo.kumo_backend.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // ===== MÉTODOS PERSONALIZADOS =====

    // Buscar items de un pedido
    List<OrderItem> findByOrderId(Long orderId);

    // Buscar items de un producto
    List<OrderItem> findByProductId(Long productId);
}