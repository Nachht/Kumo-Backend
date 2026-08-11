package com.kumo.kumo_backend.controller;

import com.kumo.kumo_backend.dto.OrderResponseDTO;
import com.kumo.kumo_backend.model.Order;
import com.kumo.kumo_backend.model.User;
import com.kumo.kumo_backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // ===== GET /api/orders =====
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<Order> orders = orderService.findAll();
        List<OrderResponseDTO> response = orders.stream()
                .map(OrderResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // ===== GET /api/orders/{id} =====
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<?> getOrderById(@PathVariable Long id, Authentication authentication) {
        try {
            Order order = orderService.findById(id);

            User user = (User) authentication.getPrincipal();
            if (!user.getRol().equalsIgnoreCase("ADMIN") && !order.getUsuarioId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "No tienes permiso para ver este pedido"));
            }

            return ResponseEntity.ok(new OrderResponseDTO(order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Pedido no encontrado"));
        }
    }

    // ===== GET /api/orders/user/{userId} =====
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<?> getOrdersByUser(@PathVariable Long userId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            if (!user.getRol().equalsIgnoreCase("ADMIN") && !user.getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "No tienes permiso para ver los pedidos de otro usuario"));
            }

            List<Order> orders = orderService.findByUser(userId);
            List<OrderResponseDTO> response = orders.stream()
                    .map(OrderResponseDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Error al obtener pedidos: " + e.getMessage()));
        }
    }

    // ===== GET /api/orders/estado/{estado} =====
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByEstado(@PathVariable String estado) {
        List<Order> orders = orderService.findByEstado(estado);
        List<OrderResponseDTO> response = orders.stream()
                .map(OrderResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // ===== POST /api/orders =====
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<?> createOrder(
            @RequestParam Long userId,
            @RequestParam String direccionEnvio,
            @RequestParam String metodoPago,
            Authentication authentication) {
        try {
            System.out.println("🔍 ===== CREANDO PEDIDO =====");
            System.out.println("🔍 userId: " + userId);
            System.out.println("🔍 direccionEnvio: " + direccionEnvio);
            System.out.println("🔍 metodoPago: " + metodoPago);

            // Verificar que el usuario autenticado sea el mismo que el userId
            User user = (User) authentication.getPrincipal();
            System.out.println("🔍 Usuario autenticado: " + user.getEmail() + " (ID: " + user.getId() + ")");
            System.out.println("🔍 Rol: " + user.getRol());

            if (!user.getRol().equalsIgnoreCase("ADMIN") && !user.getId().equals(userId)) {
                System.out.println("❌ Usuario no autorizado para crear pedido para otro usuario");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "No tienes permiso para crear pedidos para otro usuario"));
            }

            Order newOrder = orderService.createOrderFromCart(userId, direccionEnvio, metodoPago);
            System.out.println("✅ Pedido creado con ID: " + newOrder.getId());

            // 🔥 Devolver DTO
            OrderResponseDTO response = new OrderResponseDTO(newOrder);
            System.out.println("✅ Respuesta DTO creada");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            System.err.println("❌ Error al crear pedido: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al crear pedido: " + e.getMessage()));
        }
    }

    // ===== PATCH /api/orders/{id}/estado =====
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String estado) {
        try {
            Order order = orderService.updateOrderStatus(id, estado);
            return ResponseEntity.ok(new OrderResponseDTO(order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Error al actualizar estado: " + e.getMessage()));
        }
    }

    // ===== POST /api/orders/{id}/cancelar =====
    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id, Authentication authentication) {
        try {
            Order order = orderService.findById(id);

            User user = (User) authentication.getPrincipal();
            if (!user.getRol().equalsIgnoreCase("ADMIN") && !order.getUsuarioId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "No tienes permiso para cancelar este pedido"));
            }

            Order cancelledOrder = orderService.cancelOrder(id);
            return ResponseEntity.ok(new OrderResponseDTO(cancelledOrder));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Error al cancelar pedido: " + e.getMessage()));
        }
    }

    // ===== DELETE /api/orders/{id} =====
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Error al eliminar pedido: " + e.getMessage()));
        }
    }
}