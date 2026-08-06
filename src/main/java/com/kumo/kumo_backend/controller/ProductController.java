package com.kumo.kumo_backend.controller;

import com.kumo.kumo_backend.model.Product;
import com.kumo.kumo_backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;  // ✅ AGREGAR ESTE IMPORT
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    // INVITADO: Ver todos los productos (publico)
    @GetMapping("/public")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.findAll();
        return ResponseEntity.ok(products);
    }

    // INVITADO: Ver detalle de producto (publico)
    @GetMapping("/public/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        try {
            Product product = productService.findById(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ADMIN: Crear producto
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product saved = productService.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ADMIN: Actualizar producto
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product updated = productService.update(id, product);
        return ResponseEntity.ok(updated);
    }

    // ADMIN: Eliminar producto
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ===== ENDPOINTS ADICIONALES =====

    // INVITADO: Buscar por nombre
    @GetMapping("/public/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String nombre) {
        List<Product> products = productService.findByNombre(nombre);
        return ResponseEntity.ok(products);
    }

    // INVITADO: Buscar por categoria
    @GetMapping("/public/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Long categoryId) {
        List<Product> products = productService.findByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    // INVITADO: Buscar por rango de precio
    @GetMapping("/public/price-range")
    public ResponseEntity<List<Product>> getProductsByPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        List<Product> products = productService.findByPriceRange(min, max);
        return ResponseEntity.ok(products);
    }

    // INVITADO: Productos disponibles (con stock)
    @GetMapping("/public/available")
    public ResponseEntity<List<Product>> getAvailableProducts() {
        List<Product> products = productService.findAvailableProducts();
        return ResponseEntity.ok(products);
    }

    // ADMIN: Actualizar stock
    @PutMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        Product updated = productService.updateStock(id, quantity);
        return ResponseEntity.ok(updated);
    }
}