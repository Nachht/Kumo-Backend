package com.kumo.kumo_backend.controller;

import com.kumo.kumo_backend.model.User;
import com.kumo.kumo_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    // ============================================
    // CLIENTE - Gestion de su propia cuenta
    // ============================================

    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<User> getMyProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<User> updateMyProfile(@RequestBody User userUpdates) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User updated = userService.updateUserProfile(email, userUpdates);  // ✅ CORREGIDO
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/me/password")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<?> changePassword(@RequestParam String oldPassword,
                                            @RequestParam String newPassword) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        userService.changePassword(email, oldPassword, newPassword);
        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }

    // ============================================
    // ADMIN - Gestion de todos los usuarios
    // ============================================

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());  // ✅ CORREGIDO
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)  // ✅ USAR getUserById
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/rol")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> changeUserRole(@PathVariable Long id, @RequestParam String rol) {
        User updated = userService.changeUserRole(id, rol);  // ✅ CORREGIDO
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);  // ✅ CORREGIDO
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> toggleUserActive(@PathVariable Long id, @RequestParam Boolean activo) {
        User updated = userService.toggleUserActive(id, activo);  // ✅ AGREGAR NUEVO MÉTODO
        return ResponseEntity.ok(updated);
    }
}