package com.kumo.kumo_backend.dto;

import com.kumo.kumo_backend.model.Order;
import com.kumo.kumo_backend.model.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrderResponseDTO {
    private Long id;
    private Long usuarioId;
    private String estado;
    private BigDecimal total;
    private String direccionEnvio;
    private String metodoPago;
    private LocalDateTime fechaPedido;
    private List<OrderItemDTO> items;

    // 🔥 AGREGAR EL CAMPO USER
    private UserDTO user;

    public OrderResponseDTO() {}

    public OrderResponseDTO(Order order) {
        this.id = order.getId();
        this.usuarioId = order.getUsuarioId();
        this.estado = order.getEstado();
        this.total = order.getTotal();
        this.direccionEnvio = order.getDireccionEnvio();
        this.metodoPago = order.getMetodoPago();
        this.fechaPedido = order.getFechaPedido();

        // 🔥 AGREGAR EL USUARIO AL DTO
        if (order.getUser() != null) {
            this.user = new UserDTO(order.getUser());
        }

        if (order.getOrderItems() != null) {
            this.items = order.getOrderItems().stream()
                    .map(OrderItemDTO::new)
                    .collect(Collectors.toList());
        }
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getDireccionEnvio() { return direccionEnvio; }
    public void setDireccionEnvio(String direccionEnvio) { this.direccionEnvio = direccionEnvio; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public LocalDateTime getFechaPedido() { return fechaPedido; }
    public void setFechaPedido(LocalDateTime fechaPedido) { this.fechaPedido = fechaPedido; }

    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }

    // 🔥 GETTER Y SETTER PARA USER
    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }
}