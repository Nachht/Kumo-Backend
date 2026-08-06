package com.kumo.kumo_backend.dto;

import com.kumo.kumo_backend.model.CartItem;
import java.math.BigDecimal;

public class CartItemDTO {
    private Long id;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private ProductDTO product;

    public CartItemDTO() {}

    public CartItemDTO(CartItem cartItem) {
        this.id = cartItem.getId();
        this.cantidad = cartItem.getCantidad();
        this.precioUnitario = cartItem.getPrecioUnitario();
        this.subtotal = cartItem.getPrecioUnitario().multiply(BigDecimal.valueOf(cartItem.getCantidad()));

        if (cartItem.getProduct() != null) {
            this.product = new ProductDTO(cartItem.getProduct());
        }
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public ProductDTO getProduct() { return product; }
    public void setProduct(ProductDTO product) { this.product = product; }
}