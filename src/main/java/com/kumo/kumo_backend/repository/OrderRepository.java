package com.kumo.kumo_backend.repository;

import com.kumo.kumo_backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // ===== MÉTODOS PERSONALIZADOS =====

    // 🔥 NUEVO: Obtener todos los pedidos con el usuario cargado
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.user")
    List<Order> findAllWithUser();

    // Buscar pedidos por usuario
    List<Order> findByUserId(Long userId);

    // Buscar pedidos por estado
    List<Order> findByEstado(String estado);

    // Buscar pedidos por usuario y estado
    List<Order> findByUserIdAndEstado(Long userId, String estado);

    // Buscar pedidos por rango de fechas
    List<Order> findByFechaPedidoBetween(LocalDateTime start, LocalDateTime end);

    // Buscar pedidos por usuario y rango de fechas
    List<Order> findByUserIdAndFechaPedidoBetween(Long userId, LocalDateTime start, LocalDateTime end);
}