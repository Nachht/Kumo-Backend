package com.kumo.kumo_backend.service;

import com.kumo.kumo_backend.model.User;
import com.kumo.kumo_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ===== OBTENER TODOS LOS USUARIOS =====
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ===== OBTENER USUARIO POR ID =====
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // ===== OBTENER USUARIO POR EMAIL =====
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // ===== ACTUALIZAR USUARIO (COMPLETO) =====
    @Transactional
    public User updateUser(Long id, String nombre, String telefono, String direccion,
                           String rol, Boolean activo, String password) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (nombre != null && !nombre.isEmpty()) {
            user.setNombre(nombre);
        }
        if (telefono != null && !telefono.isEmpty()) {
            user.setTelefono(telefono);
        }
        if (direccion != null && !direccion.isEmpty()) {
            user.setDireccion(direccion);
        }
        if (rol != null && !rol.isEmpty()) {
            user.setRol(rol);
        }
        if (activo != null) {
            user.setActivo(activo);
        }
        if (password != null && !password.isEmpty()) {
            if (password.length() < 6) {
                throw new RuntimeException("La contraseña debe tener al menos 6 caracteres");
            }
            user.setPassword(passwordEncoder.encode(password));
        }

        return userRepository.save(user);
    }

    // ===== CAMBIAR CONTRASEÑA =====
    @Transactional
    public void changePassword(Long id, String passwordActual, String passwordNueva, boolean esAdmin) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Si no es admin, verificar la contraseña actual
        if (!esAdmin) {
            if (passwordActual == null || !passwordEncoder.matches(passwordActual, user.getPassword())) {
                throw new RuntimeException("Contraseña actual incorrecta");
            }
        }

        if (passwordNueva == null || passwordNueva.length() < 6) {
            throw new RuntimeException("La nueva contraseña debe tener al menos 6 caracteres");
        }

        user.setPassword(passwordEncoder.encode(passwordNueva));
        userRepository.save(user);
    }

    // ===== CAMBIAR ROL =====
    @Transactional
    public User changeUserRole(Long id, String rol) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setRol(rol);
        return userRepository.save(user);
    }

    // ===== ACTIVAR/DESACTIVAR USUARIO =====
    @Transactional
    public User toggleUserActive(Long id, Boolean activo) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setActivo(activo);
        return userRepository.save(user);
    }

    // ===== ELIMINAR USUARIO =====
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        userRepository.delete(user);
    }
}