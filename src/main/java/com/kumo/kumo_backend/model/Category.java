package com.kumo.kumo_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categorias")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(unique = true, nullable = false, length = 50)
    private String nombre;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean activo = true;

    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    // ===== RELACIONES =====
    // En Category.java, agrega esta anotación para romper el bucle infinito
    @JsonIgnore
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

    // ===== CONSTRUCTORES =====
    public Category() {}

    public Category(String nombre) {
        this.nombre = nombre;
        this.fechaRegistro = LocalDateTime.now();
        this.activo = true;
    }

    public Category(String nombre, Boolean activo) {
        this.nombre = nombre;
        this.activo = activo;
        this.fechaRegistro = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Boolean getActivo() {
        return activo;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    // ===== MÉTODOS AUXILIARES =====
    public void addProduct(Product product) {
        products.add(product);
        product.setCategory(this);
    }

    public void removeProduct(Product product) {
        products.remove(product);
        product.setCategory(null);
    }

    @PrePersist
    protected void onCreate() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
    }
}