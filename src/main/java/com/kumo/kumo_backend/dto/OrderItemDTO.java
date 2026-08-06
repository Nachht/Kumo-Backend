package com.kumo.kumo_backend.dto;

import com.kumo.kumo_backend.model.OrderItem;
import java.math.BigDecimal;

public class OrderItemDTO {
    private Long id;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private ProductDTO product;

    public OrderItemDTO() {}

    public OrderItemDTO(OrderItem orderItem) {
        this.id = orderItem.getId();
        this.cantidad = orderItem.getCantidad();
        this.precioUnitario = orderItem.getPrecioUnitario();
        this.subtotal = orderItem.getPrecioUnitario().multiply(BigDecimal.valueOf(orderItem.getCantidad()));

        if (orderItem.getProduct() != null) {
            this.product = new ProductDTO(orderItem.getProduct());
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