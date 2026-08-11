package com.kumo.kumo_backend.dto;

import com.kumo.kumo_backend.model.Cart;
import com.kumo.kumo_backend.model.CartItem;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CartDTO {
    private Long id;
    private String estado;
    private BigDecimal total;
    private LocalDateTime fechaCreacion;
    private UserDTO user;
    private List<CartItemDTO> items;

    public CartDTO() {}

    public CartDTO(Cart cart) {
        this.id = cart.getId();
        this.estado = cart.getEstado();
        this.fechaCreacion = cart.getFechaCreacion();

        // 🔥 CALCULAR EL TOTAL MANUALMENTE
        this.total = cart.getCartItems().stream()
                .map(item -> item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (cart.getUser() != null) {
            this.user = new UserDTO(cart.getUser());
        }

        if (cart.getCartItems() != null) {
            this.items = cart.getCartItems().stream()
                    .map(CartItemDTO::new)
                    .collect(Collectors.toList());
        }
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }

    public List<CartItemDTO> getItems() { return items; }
    public void setItems(List<CartItemDTO> items) { this.items = items; }
}