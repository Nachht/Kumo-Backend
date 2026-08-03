package com.kumo.kumo_backend.repository;

import com.kumo.kumo_backend.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // ===== MÉTODOS PERSONALIZADOS =====

    // Buscar carrito activo de un usuario
    Optional<Cart> findByUserIdAndEstado(Long userId, String estado);

    // Buscar todos los carritos de un usuario
    List<Cart> findByUserId(Long userId);

    // Buscar carritos por estado
    List<Cart> findByEstado(String estado);

    // Buscar carritos activos de todos los usuarios
    List<Cart> findByEstadoAndUserId(String estado, Long userId);
}