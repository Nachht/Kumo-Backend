package com.kumo.kumo_backend.controller;

import com.kumo.kumo_backend.model.Category;
import com.kumo.kumo_backend.model.Product;
import com.kumo.kumo_backend.service.CategoryService;
import com.kumo.kumo_backend.service.ProductService;
import io.swagger.v3.oas.annotations.Hidden;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/public")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        try {
            Product product = productService.findById(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            logger.error("Producto no encontrado con ID: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    // ===== ADMIN: Crear producto (JSON) =====
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        try {
            logger.info("📝 Producto recibido:");
            logger.info("   Nombre: {}", product.getNombre());
            logger.info("   Categoría (String): {}", product.getCategoria());
            logger.info("   Precio: {}", product.getPrecio());
            logger.info("   Stock: {}", product.getStock());

            // 🔥 BUSCAR Y ASIGNAR CATEGORÍA
            if (product.getCategoria() != null && !product.getCategoria().isEmpty()) {
                String nombreCategoria = product.getCategoria();
                logger.info("🔍 Buscando categoría por nombre: {}", nombreCategoria);

                Category category = categoryService.findByNombre(nombreCategoria);
                if (category == null) {
                    logger.warn("❌ Categoría no encontrada: {}", nombreCategoria);
                    return ResponseEntity.badRequest().body("Categoría no encontrada: " + nombreCategoria);
                }
                product.setCategory(category);
                logger.info("✅ Categoría asignada: {} (ID: {})", category.getNombre(), category.getId());
            } else {
                logger.warn("⚠️ No se envió categoría");
            }

            // Si la imagen es base64 o null, guardarla como está
            if (product.getImagen() == null || product.getImagen().isEmpty()) {
                product.setImagen("logo.png");
            }

            Product saved = productService.save(product);
            logger.info("✅ Producto guardado con categoría ID: {}", saved.getCategoriaId());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (Exception e) {
            logger.error("❌ Error al crear producto: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear producto: " + e.getMessage());
        }
    }

    // ===== ADMIN: Actualizar producto =====
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {
        try {
            Product existingProduct = productService.findById(id);

            // Actualizar campos básicos
            existingProduct.setNombre(product.getNombre());
            existingProduct.setDescripcion(product.getDescripcion());
            existingProduct.setPrecio(product.getPrecio());
            existingProduct.setStock(product.getStock());
            existingProduct.setImagen(product.getImagen());

            // ✅ 🔥 ACTUALIZAR ESTADO
            existingProduct.setActivo(product.getActivo());

            // Actualizar categoría
            if (product.getCategoria() != null && !product.getCategoria().isEmpty()) {
                Category category = categoryService.findByNombre(product.getCategoria());
                if (category == null) {
                    logger.warn("❌ Categoría no encontrada: {}", product.getCategoria());
                    return ResponseEntity.badRequest().body("Categoría no encontrada: " + product.getCategoria());
                }
                existingProduct.setCategory(category);
            }

            // Si no se envía imagen, mantener la existente
            if (product.getImagen() == null || product.getImagen().isEmpty()) {
                existingProduct.setImagen(existingProduct.getImagen());
            }

            Product updated = productService.update(id, existingProduct);
            logger.info("✅ Producto actualizado: {}", updated.getNombre());
            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            logger.error("❌ Error al actualizar producto: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar producto: " + e.getMessage());
        }
    }

    // ===== ADMIN: Cambiar estado del producto (Activo/Inactivo) =====
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleProductStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> payload) {
        try {
            Boolean activo = payload.get("activo");
            if (activo == null) {
                logger.warn("⚠️ El campo 'activo' es requerido");
                return ResponseEntity.badRequest().body("El campo 'activo' es requerido");
            }

            logger.info("🔄 Cambiando estado del producto ID {} a: {}", id, activo);

            Product product = productService.findById(id);
            product.setActivo(activo);
            Product updated = productService.update(id, product);

            logger.info("✅ Estado actualizado para el producto: {}", updated.getNombre());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("❌ Error al cambiar estado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al cambiar estado: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Hidden
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteById(id);
            logger.info("✅ Producto eliminado con ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("❌ Producto no encontrado con ID: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/public/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String nombre) {
        return ResponseEntity.ok(productService.findByNombre(nombre));
    }

    @GetMapping("/public/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.findByCategory(categoryId));
    }

    @GetMapping("/public/price-range")
    public ResponseEntity<List<Product>> getProductsByPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        return ResponseEntity.ok(productService.findByPriceRange(min, max));
    }

    @GetMapping("/public/available")
    public ResponseEntity<List<Product>> getAvailableProducts() {
        return ResponseEntity.ok(productService.findAvailableProducts());
    }

    @PutMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        Product updated = productService.updateStock(id, quantity);
        logger.info("✅ Stock actualizado para producto ID {}: {}", id, updated.getStock());
        return ResponseEntity.ok(updated);
    }
}