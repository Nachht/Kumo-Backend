package com.kumo.kumo_backend.controller;

import com.kumo.kumo_backend.model.Order;
import com.kumo.kumo_backend.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ===== GET /api/orders =====
    // Listar todos los pedidos
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.findAll());
    }

    // ===== GET /api/orders/{id} =====
    // Obtener pedido por ID
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    // ===== GET /api/orders/user/{userId} =====
    // Obtener pedidos por usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.findByUser(userId));
    }

    // ===== GET /api/orders/estado/{estado} =====
    // Obtener pedidos por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Order>> getOrdersByEstado(@PathVariable String estado) {
        return ResponseEntity.ok(orderService.findByEstado(estado));
    }

    // ===== POST /api/orders =====
    // Crear pedido desde carrito
    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestParam Long userId,
            @RequestParam String direccionEnvio,
            @RequestParam String metodoPago) {
        Order newOrder = orderService.createOrderFromCart(userId, direccionEnvio, metodoPago);
        return ResponseEntity.status(HttpStatus.CREATED).body(newOrder);
    }

    // ===== PATCH /api/orders/{id}/estado =====
    // Actualizar estado del pedido
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String estado) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, estado));
    }

    // ===== POST /api/orders/{id}/cancelar =====
    // Cancelar pedido
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }

    // ===== DELETE /api/orders/{id} =====
    // Eliminar pedido (solo si está pendiente)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}