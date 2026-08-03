package com.kumo.kumo_backend.repository;

import com.kumo.kumo_backend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // ===== MÉTODOS PERSONALIZADOS =====

    // Buscar categoría por nombre exacto
    Optional<Category> findByNombre(String nombre);

    // Buscar categorías por nombre (contiene)
    List<Category> findByNombreContainingIgnoreCase(String nombre);

    // Buscar categorías activas
    List<Category> findByActivoTrue();

    // Buscar categorías inactivas
    List<Category> findByActivoFalse();

    // Verificar si existe una categoría con ese nombre
    boolean existsByNombre(String nombre);
}