package com.kumo.kumo_backend.controller;

import com.kumo.kumo_backend.model.User;
import com.kumo.kumo_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // ===== GET /api/users (SOLO ADMIN) =====
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // ===== GET /api/users/{id} =====
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENTE')")
    public ResponseEntity<?> getUserById(@PathVariable Long id, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            if (!user.getRol().equalsIgnoreCase("ADMIN") && !user.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "No tienes permiso para ver este perfil"));
            }

            User found = userService.getUserById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (!user.getRol().equalsIgnoreCase("ADMIN")) {
                found.setPassword(null);
            }

            return ResponseEntity.ok(found);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ===== GET /api/users/email/{email} =====
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENTE')")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            User found = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (!user.getRol().equalsIgnoreCase("ADMIN") && !user.getEmail().equals(email)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "No tienes permiso para ver este perfil"));
            }

            if (!user.getRol().equalsIgnoreCase("ADMIN")) {
                found.setPassword(null);
            }

            return ResponseEntity.ok(found);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ===== PUT /api/users/{id} (ACTUALIZAR PERFIL) =====
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENTE')")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();

            if (!user.getRol().equalsIgnoreCase("ADMIN") && !user.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "No tienes permiso para editar este perfil"));
            }

            String nombre = request.containsKey("nombre") ? request.get("nombre").toString() : null;
            String telefono = request.containsKey("telefono") ? request.get("telefono").toString() : null;
            String direccion = request.containsKey("direccion") ? request.get("direccion").toString() : null;
            String rol = request.containsKey("rol") ? request.get("rol").toString() : null;
            Boolean activo = request.containsKey("activo") ? (Boolean) request.get("activo") : null;
            String password = request.containsKey("password") ? request.get("password").toString() : null;

            User updated = userService.updateUser(id, nombre, telefono, direccion, rol, activo, password);

            if (!user.getRol().equalsIgnoreCase("ADMIN")) {
                updated.setPassword(null);
            }

            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ===== PATCH /api/users/{id}/password (CAMBIAR CONTRASEÑA) =====
    @PatchMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENTE')")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();

            if (!user.getRol().equalsIgnoreCase("ADMIN") && !user.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "No tienes permiso para cambiar esta contraseña"));
            }

            String passwordActual = request.get("passwordActual");
            String passwordNueva = request.get("passwordNueva");
            boolean esAdmin = user.getRol().equalsIgnoreCase("ADMIN");

            userService.changePassword(id, passwordActual, passwordNueva, esAdmin);

            return ResponseEntity.ok(Map.of("message", "Contraseña actualizada exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ===== PUT /api/users/{id}/role (SOLO ADMIN) =====
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changeUserRole(@PathVariable Long id, @RequestParam String rol) {
        try {
            User updated = userService.changeUserRole(id, rol);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ===== PATCH /api/users/{id}/active (SOLO ADMIN) =====
    @PatchMapping("/{id}/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleUserActive(@PathVariable Long id, @RequestParam Boolean activo) {
        try {
            User updated = userService.toggleUserActive(id, activo);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ===== DELETE /api/users/{id} (SOLO ADMIN) =====
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}