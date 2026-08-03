package com.kumo.kumo_backend.repository;

import com.kumo.kumo_backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ===== MÉTODOS PERSONALIZADOS =====

    // Buscar productos por nombre (contiene)
    List<Product> findByNombreContainingIgnoreCase(String nombre);

    // Buscar productos por categoría
    List<Product> findByCategoryId(Long categoryId);

    // Buscar productos por rango de precio
    List<Product> findByPrecioBetween(BigDecimal min, BigDecimal max);

    // Buscar productos con stock disponible (mayor a 0)
    List<Product> findByStockGreaterThan(Integer stock);

    // Buscar productos activos
    List<Product> findByActivoTrue();

    // Buscar productos inactivos
    List<Product> findByActivoFalse();

    // Buscar productos con stock bajo (para alertas)
    List<Product> findByStockLessThan(Integer stock);

    // Buscar productos por categoría y activos
    List<Product> findByCategoryIdAndActivoTrue(Long categoryId);

    // Buscar productos con precio menor a
    List<Product> findByPrecioLessThan(BigDecimal precio);

    // Buscar productos con precio mayor a
    List<Product> findByPrecioGreaterThan(BigDecimal precio);
}