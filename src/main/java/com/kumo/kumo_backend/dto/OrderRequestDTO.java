package com.kumo.kumo_backend.dto;

public class OrderRequestDTO {
    private Long userId;
    private String direccionEnvio;
    private String metodoPago;

    public OrderRequestDTO() {}

    // Getters y Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getDireccionEnvio() { return direccionEnvio; }
    public void setDireccionEnvio(String direccionEnvio) { this.direccionEnvio = direccionEnvio; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
}