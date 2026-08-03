package com.kumo.kumo_backend.model;

// ❌ ELIMINA ESTE IMPORT
// import com.kumo.kumo_backend.enums.Role;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class User {

    // ===== ATRIBUTOS =====
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 80)
    @Column(name = "nombres", nullable = false, length = 80)
    private String names;

    @NotBlank
    @Email
    @Size(max = 120)
    @Column(unique = true, nullable = false, length = 120)
    private String email;

    @NotBlank
    @Size(max = 255)
    @Column(name = "contraseña", nullable = false, length = 255)
    private String password;

    @Size(max = 20)
    @Column(length = 20)
    private String celular;

    // 🔥 CAMBIADO: de Role a String
    @Column(columnDefinition = "VARCHAR(20) DEFAULT 'cliente'")
    private String rol = "cliente";

    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_ultimo_acceso")
    private LocalDateTime fechaUltimoAcceso;

    @Column(columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean activo = true;

    // ===== RELACIONES =====
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cart> carts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    // ===== CONSTRUCTORES =====
    public User() {}

    public User(String names, String email, String password, String celular) {
        this.names = names;
        this.email = email;
        this.password = password;
        this.celular = celular;
    }

    // ===== MÉTODOS AUXILIARES =====
    public void addCart(Cart cart) {
        carts.add(cart);
        cart.setUser(this);
    }

    public void removeCart(Cart cart) {
        carts.remove(cart);
        cart.setUser(null);
    }

    public void addOrder(Order order) {
        orders.add(order);
        order.setUser(this);
    }

    public void removeOrder(Order order) {
        orders.remove(order);
        order.setUser(null);
    }

    // ===== GETTERS Y SETTERS =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNames() { return names; }
    public void setNames(String names) { this.names = names; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }

    // 🔥 CAMBIADO: getter devuelve String
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public LocalDateTime getFechaUltimoAcceso() { return fechaUltimoAcceso; }
    public void setFechaUltimoAcceso(LocalDateTime fechaUltimoAcceso) { this.fechaUltimoAcceso = fechaUltimoAcceso; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public List<Cart> getCarts() { return carts; }
    public void setCarts(List<Cart> carts) { this.carts = carts; }

    public List<Order> getOrders() { return orders; }
    public void setOrders(List<Order> orders) { this.orders = orders; }

    // ===== MÉTODOS JPA =====
    @PrePersist
    protected void onCreate() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
        // 🔥 CAMBIADO: valor por defecto como String
        if (rol == null || rol.isEmpty()) {
            rol = "cliente";
        }
    }

    // ===== toString =====
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", names='" + names + '\'' +
                ", email='" + email + '\'' +
                ", rol='" + rol + '\'' +
                ", activo=" + activo +
                '}';
    }
}