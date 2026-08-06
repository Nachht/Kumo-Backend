package com.kumo.kumo_backend.dto;

import com.kumo.kumo_backend.model.User;
import java.time.LocalDateTime;

public class UserDTO {
    private Long id;
    private String nombre;
    private String email;
    private String rol;
    private String telefono;
    private String direccion;
    private Boolean activo;
    private LocalDateTime fechaRegistro;

    public UserDTO() {}

    public UserDTO(User user) {
        this.id = user.getId();
        this.nombre = user.getNombre();
        this.email = user.getEmail();
        this.rol = user.getRol();
        this.telefono = user.getTelefono();
        this.direccion = user.getDireccion();
        this.activo = user.getActivo();
        this.fechaRegistro = user.getFechaRegistro();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}