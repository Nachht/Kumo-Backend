package com.kumo.kumo_backend.dto;

import com.kumo.kumo_backend.model.Product;
import java.math.BigDecimal;

public class ProductDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private String imagen;
    private Boolean activo;
    private CategoryDTO category;

    public ProductDTO() {}

    public ProductDTO(Product product) {
        this.id = product.getId();
        this.nombre = product.getNombre();
        this.descripcion = product.getDescripcion();
        this.precio = product.getPrecio();
        this.stock = product.getStock();
        this.imagen = product.getImagen();
        this.activo = product.getActivo();
        if (product.getCategory() != null) {
            this.category = new CategoryDTO(product.getCategory());
        }
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public CategoryDTO getCategory() { return category; }
    public void setCategory(CategoryDTO category) { this.category = category; }
}