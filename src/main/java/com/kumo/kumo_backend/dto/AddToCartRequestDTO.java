package com.kumo.kumo_backend.dto;

public class AddToCartRequestDTO {
    private Long userId;
    private Long productoId;
    private Integer cantidad;

    public AddToCartRequestDTO() {}

    // Getters y Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}