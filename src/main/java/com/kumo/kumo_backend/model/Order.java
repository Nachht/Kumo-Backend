package com.kumo.kumo_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "usuario_id", insertable = false, updatable = false)
    private Long usuarioId;

    @Column(name = "fecha_pedido", updatable = false)
    private LocalDateTime fechaPedido;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;


    @Column(columnDefinition = "VARCHAR(20) DEFAULT 'pendiente'")
    private String estado = "pendiente";

    @NotBlank
    @Column(name = "direccion_envio", nullable = false, columnDefinition = "TEXT")
    private String direccionEnvio;

    @Column(name = "metodo_pago", length = 50)
    private String metodoPago;

    // ===== RELACIONES =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    // ===== CONSTRUCTORES =====
    public Order() {}

    public Order(User user, String direccionEnvio, String metodoPago) {
        this.user = user;
        this.direccionEnvio = direccionEnvio;
        this.metodoPago = metodoPago;
        this.fechaPedido = LocalDateTime.now();
        this.estado = "pendiente";
        this.total = BigDecimal.ZERO;
    }

    // ===== MÉTODOS AUXILIARES =====
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
        recalculateTotal();
    }

    public void removeOrderItem(OrderItem item) {
        orderItems.remove(item);
        item.setOrder(null);
        recalculateTotal();
    }

    private void recalculateTotal() {
        this.total = orderItems.stream()
                .map(item -> item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ===== GETTERS Y SETTERS =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public LocalDateTime getFechaPedido() { return fechaPedido; }
    public void setFechaPedido(LocalDateTime fechaPedido) { this.fechaPedido = fechaPedido; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    // 🔥 GETTER Y SETTER para estado (ahora String)
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getDireccionEnvio() { return direccionEnvio; }
    public void setDireccionEnvio(String direccionEnvio) { this.direccionEnvio = direccionEnvio; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }

    @PrePersist
    protected void onCreate() {
        if (fechaPedido == null) {
            fechaPedido = LocalDateTime.now();
        }
        if (estado == null || estado.isEmpty()) {
            estado = "pendiente";  // ← String por defecto
        }
        if (total == null) {
            total = BigDecimal.ZERO;
        }
    }
}