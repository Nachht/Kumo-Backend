package com.kumo.kumo_backend.dto;

public class AddToCartRequestDTO {
    private Long productId;  // ← Cambiado de productoId a productId
    private Integer cantidad;

    public AddToCartRequestDTO() {}

    // Getters y Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    @Override
    public String toString() {
        return "AddToCartRequestDTO{productId=" + productId + ", cantidad=" + cantidad + "}";
    }
}