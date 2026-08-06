package com.kumo.kumo_backend.dto;

import com.kumo.kumo_backend.model.Category;

public class CategoryDTO {
    private Long id;
    private String nombre;
    private Boolean activo;

    public CategoryDTO() {}

    public CategoryDTO(Category category) {
        this.id = category.getId();
        this.nombre = category.getNombre();
        this.activo = category.getActivo();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}