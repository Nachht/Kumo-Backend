package com.kumo.kumo_backend.dto;

import com.kumo.kumo_backend.model.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrderDTO {
    private Long id;
    private LocalDateTime fechaPedido;
    private BigDecimal total;
    private String estado;
    private String direccionEnvio;
    private String metodoPago;
    private UserDTO user;
    private List<OrderItemDTO> items;

    public OrderDTO() {}

    public OrderDTO(Order order) {
        this.id = order.getId();
        this.fechaPedido = order.getFechaPedido();
        this.total = order.getTotal();
        this.estado = order.getEstado();
        this.direccionEnvio = order.getDireccionEnvio();
        this.metodoPago = order.getMetodoPago();

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

    public LocalDateTime getFechaPedido() { return fechaPedido; }
    public void setFechaPedido(LocalDateTime fechaPedido) { this.fechaPedido = fechaPedido; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getDireccionEnvio() { return direccionEnvio; }
    public void setDireccionEnvio(String direccionEnvio) { this.direccionEnvio = direccionEnvio; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }

    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }
}