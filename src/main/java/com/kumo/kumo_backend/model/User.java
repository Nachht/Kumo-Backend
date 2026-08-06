package com.kumo.kumo_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class User {  // ← SOLO UNA DECLARACIÓN DE CLASE

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank
    @Email
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Column(name = "rol", columnDefinition = "VARCHAR(20) DEFAULT 'CLIENTE'")
    private String rol = "CLIENTE";

    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "activo")
    private Boolean activo = true;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
        if (rol == null) {
            rol = "CLIENTE";
        }
        if (activo == null) {
            activo = true;
        }
    }

    // ===== GETTERS Y SETTERS =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
} // ← FIN DE LA CLASE (SOLO UNO)