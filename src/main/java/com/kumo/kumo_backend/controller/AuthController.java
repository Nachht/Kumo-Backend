package com.kumo.kumo_backend.controller;

import com.kumo.kumo_backend.dto.AuthRequest;
import com.kumo.kumo_backend.dto.AuthResponse;
import com.kumo.kumo_backend.dto.RegisterRequest;
import com.kumo.kumo_backend.model.User;
import com.kumo.kumo_backend.repository.UserRepository;
import com.kumo.kumo_backend.service.CartService;  // ← 🔥 AGREGAR ESTE IMPORT
import com.kumo.kumo_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CartService cartService;  // ← Ahora funciona

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            System.out.println("📝 Registrando usuario: " + registerRequest.getEmail());

            if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body("El email ya está registrado");
            }

            User user = new User();
            user.setNombre(registerRequest.getNombre());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setRol("CLIENTE");
            user.setActivo(true);

            if (registerRequest.getTelefono() != null) {
                user.setTelefono(registerRequest.getTelefono());
            }
            if (registerRequest.getDireccion() != null) {
                user.setDireccion(registerRequest.getDireccion());
            }

            User savedUser = userRepository.save(user);
            System.out.println("✅ Usuario guardado con ID: " + savedUser.getId());

            // ✅ CREAR CARRITO AUTOMÁTICAMENTE
            cartService.createCartForUser(savedUser.getId());
            System.out.println("🛒 Carrito creado para usuario ID: " + savedUser.getId());

            System.out.println("✅ Usuario registrado exitosamente: " + user.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado exitosamente");

        } catch (Exception e) {
            System.err.println("❌ Error al registrar usuario: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar usuario: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );

            final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
            final String jwt = jwtUtil.generateToken(userDetails);

            User user = userRepository.findByEmail(authRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            return ResponseEntity.ok(new AuthResponse(
                    jwt,
                    user.getEmail(),
                    user.getRol(),
                    user.getNombre()
            ));
        } catch (Exception e) {
            System.err.println("❌ Error en login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales inválidas");
        }
    }
}