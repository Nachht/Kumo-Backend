package com.kumo.kumo_backend.service;

import com.kumo.kumo_backend.model.Category;
import com.kumo.kumo_backend.model.Product;
import com.kumo.kumo_backend.repository.CategoryRepository;
import com.kumo.kumo_backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // ===== LISTAR TODOS LOS PRODUCTOS =====
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    // ===== BUSCAR PRODUCTO POR ID =====
    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    // ===== BUSCAR PRODUCTOS POR NOMBRE =====
    @Transactional(readOnly = true)
    public List<Product> findByNombre(String nombre) {
        return productRepository.findByNombreContainingIgnoreCase(nombre);
    }

    // ===== BUSCAR PRODUCTOS POR CATEGORÍA =====
    @Transactional(readOnly = true)
    public List<Product> findByCategory(Long categoryId) {
        // Verificar que la categoría existe
        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Categoría no encontrada con ID: " + categoryId);
        }
        return productRepository.findByCategoryId(categoryId);
    }

    // ===== BUSCAR PRODUCTOS POR RANGO DE PRECIO =====
    @Transactional(readOnly = true)
    public List<Product> findByPriceRange(BigDecimal min, BigDecimal max) {
        if (min.compareTo(max) > 0) {
            throw new RuntimeException("El precio mínimo no puede ser mayor al máximo");
        }
        return productRepository.findByPrecioBetween(min, max);
    }

    // ===== BUSCAR PRODUCTOS CON STOCK DISPONIBLE =====
    @Transactional(readOnly = true)
    public List<Product> findAvailableProducts() {
        return productRepository.findByStockGreaterThan(0);
    }

    // ===== BUSCAR PRODUCTOS ACTIVOS =====
    @Transactional(readOnly = true)
    public List<Product> findActiveProducts() {
        return productRepository.findByActivoTrue();
    }

    // ===== CREAR PRODUCTO =====
    @Transactional
    public Product save(Product product) {
        // Validar que la categoría existe
        if (product.getCategory() != null) {
            Category category = categoryRepository.findById(product.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + product.getCategory().getId()));
            product.setCategory(category);
        }

        // Validar precio positivo
        if (product.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El precio debe ser mayor a 0");
        }

        // 🔥 FORZAR QUE EL PRODUCTO SE CREE INACTIVO
        product.setActivo(false);
        System.out.println("🔴 Producto creado en estado INACTIVO: " + product.getNombre());

        return productRepository.save(product);
    }

    // ===== ACTUALIZAR PRODUCTO =====
    @Transactional
    public Product update(Long id, Product productDetails) {
        Product existingProduct = findById(id);

        // Actualizar todos los campos
        existingProduct.setNombre(productDetails.getNombre());
        existingProduct.setDescripcion(productDetails.getDescripcion());
        existingProduct.setPrecio(productDetails.getPrecio());
        existingProduct.setStock(productDetails.getStock());
        existingProduct.setImagen(productDetails.getImagen());

        // ✅ 🔥 ACTUALIZAR ESTADO
        existingProduct.setActivo(productDetails.getActivo());

        // Si se cambia la categoría, verificar que existe
        if (productDetails.getCategory() != null) {
            Category category = categoryRepository.findById(productDetails.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + productDetails.getCategory().getId()));
            existingProduct.setCategory(category);
        }

        return productRepository.save(existingProduct);
    }

    // ===== ELIMINAR PRODUCTO =====
    @Transactional
    public void deleteById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
        productRepository.deleteById(id);
    }

    // ===== ACTUALIZAR STOCK =====
    @Transactional
    public Product updateStock(Long id, Integer quantity) {
        Product product = findById(id);
        int newStock = product.getStock() + quantity;
        if (newStock < 0) {
            throw new RuntimeException("Stock insuficiente. Stock actual: " + product.getStock());
        }
        product.setStock(newStock);
        return productRepository.save(product);
    }

    // ===== DESCONTAR STOCK (para pedidos) =====
    @Transactional
    public void deductStock(Long productId, Integer quantity) {
        Product product = findById(productId);
        if (product.getStock() < quantity) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + product.getStock() + ", solicitado: " + quantity);
        }
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }
}