package com.kumo.kumo_backend.service;

import com.kumo.kumo_backend.model.Category;
import com.kumo.kumo_backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // ===== LISTAR TODAS LAS CATEGORÍAS =====
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    // ===== BUSCAR CATEGORÍA POR ID =====
    @Transactional(readOnly = true)
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
    }

    // ===== BUSCAR CATEGORÍA POR NOMBRE =====
    @Transactional(readOnly = true)
    public Category findByNombre(String nombre) {
        return categoryRepository.findByNombre(nombre)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada: " + nombre));
    }

    // ===== CREAR CATEGORÍA =====
    @Transactional
    public Category save(Category category) {
        // Validar que el nombre no exista
        if (categoryRepository.existsByNombre(category.getNombre())) {
            throw new RuntimeException("Ya existe una categoría con el nombre: " + category.getNombre());
        }
        return categoryRepository.save(category);
    }

    // ===== ACTUALIZAR CATEGORÍA =====
    @Transactional
    public Category update(Long id, Category categoryDetails) {
        Category existingCategory = findById(id);

        // Si se cambia el nombre, verificar que no esté en uso
        if (!existingCategory.getNombre().equals(categoryDetails.getNombre())) {
            if (categoryRepository.existsByNombre(categoryDetails.getNombre())) {
                throw new RuntimeException("Ya existe una categoría con el nombre: " + categoryDetails.getNombre());
            }
            existingCategory.setNombre(categoryDetails.getNombre());
        }

        existingCategory.setActivo(categoryDetails.getActivo());
        return categoryRepository.save(existingCategory);
    }

    // ===== ELIMINAR CATEGORÍA =====
    @Transactional
    public void deleteById(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Categoría no encontrada con ID: " + id);
        }
        categoryRepository.deleteById(id);
    }

    // ===== BUSCAR CATEGORÍAS ACTIVAS =====
    @Transactional(readOnly = true)
    public List<Category> findActiveCategories() {
        return categoryRepository.findByActivoTrue();
    }
}