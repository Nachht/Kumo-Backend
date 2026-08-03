package com.kumo.kumo_backend.service;

// ❌ ELIMINA ESTE IMPORT
// import com.kumo.kumo_backend.enums.Role;

import com.kumo.kumo_backend.model.User;
import com.kumo.kumo_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
    }

    @Transactional
    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("El email ya está registrado: " + user.getEmail());
        }

        user.setFechaRegistro(LocalDateTime.now());
        user.setActivo(true);
        // 🔥 CAMBIADO: valor por defecto como String
        if (user.getRol() == null || user.getRol().isEmpty()) {
            user.setRol("cliente");
        }

        return userRepository.save(user);
    }

    @Transactional
    public User update(Long id, User userDetails) {
        User existingUser = findById(id);

        existingUser.setNames(userDetails.getNames());
        existingUser.setCelular(userDetails.getCelular());

        if (!existingUser.getEmail().equals(userDetails.getEmail())) {
            if (userRepository.existsByEmail(userDetails.getEmail())) {
                throw new RuntimeException("El email ya está registrado por otro usuario: " + userDetails.getEmail());
            }
            existingUser.setEmail(userDetails.getEmail());
        }

        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            existingUser.setPassword(userDetails.getPassword());
        }

        // 🔥 CAMBIADO: actualizar rol como String
        if (userDetails.getRol() != null && !userDetails.getRol().isEmpty()) {
            existingUser.setRol(userDetails.getRol());
        }

        return userRepository.save(existingUser);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public User deactivate(Long id) {
        User user = findById(id);
        user.setActivo(false);
        return userRepository.save(user);
    }

    @Transactional
    public User activate(Long id) {
        User user = findById(id);
        user.setActivo(true);
        return userRepository.save(user);
    }

    @Transactional
    public User updateLastAccess(Long id) {
        User user = findById(id);
        user.setFechaUltimoAcceso(LocalDateTime.now());
        return userRepository.save(user);
    }

    // 🔥 CAMBIADO: de Role a String
    @Transactional(readOnly = true)
    public List<User> findByRol(String rol) {
        return userRepository.findByRol(rol);
    }

    @Transactional(readOnly = true)
    public List<User> findActiveUsers() {
        return userRepository.findByActivoTrue();
    }
}