package com.kumo.kumo_backend.service;

import com.kumo.kumo_backend.model.User;
import com.kumo.kumo_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ============================================
    // CLIENTE
    // ============================================

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User updateUserProfile(String email, User updates) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (updates.getNombre() != null) {
            user.setNombre(updates.getNombre());
        }
        if (updates.getTelefono() != null) {
            user.setTelefono(updates.getTelefono());
        }
        if (updates.getDireccion() != null) {
            user.setDireccion(updates.getDireccion());
        }

        return userRepository.save(user);
    }

    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Contraseña actual incorrecta");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // ============================================
    // ADMIN
    // ============================================

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {  // ✅ NUEVO MÉTODO
        return userRepository.findById(id);
    }

    public User changeUserRole(Long userId, String rol) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!rol.equals("ADMIN") && !rol.equals("CLIENTE")) {
            throw new RuntimeException("Rol inválido. Use: ADMIN o CLIENTE");
        }

        user.setRol(rol);
        return userRepository.save(user);
    }

    public User toggleUserActive(Long userId, Boolean activo) {  // ✅ NUEVO MÉTODO
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));
        user.setActivo(activo);
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}