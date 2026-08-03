package com.kumo.kumo_backend.controller;

import com.kumo.kumo_backend.model.Category;
import com.kumo.kumo_backend.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // ===== GET /api/categories =====
    // Listar todas las categorías
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    // ===== GET /api/categories/{id} =====
    // Obtener categoría por ID
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    // ===== GET /api/categories/nombre/{nombre} =====
    // Obtener categoría por nombre
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Category> getCategoryByNombre(@PathVariable String nombre) {
        return ResponseEntity.ok(categoryService.findByNombre(nombre));
    }

    // ===== GET /api/categories/activas =====
    // Obtener categorías activas
    @GetMapping("/activas")
    public ResponseEntity<List<Category>> getActiveCategories() {
        return ResponseEntity.ok(categoryService.findActiveCategories());
    }

    // ===== POST /api/categories =====
    // Crear nueva categoría
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.save(category));
    }

    // ===== PUT /api/categories/{id} =====
    // Actualizar categoría
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        return ResponseEntity.ok(categoryService.update(id, category));
    }

    // ===== DELETE /api/categories/{id} =====
    // Eliminar categoría
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}