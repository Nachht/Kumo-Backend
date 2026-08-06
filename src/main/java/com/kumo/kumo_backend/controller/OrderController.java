package com.kumo.kumo_backend.controller;

import com.kumo.kumo_backend.model.Order;
import com.kumo.kumo_backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // ✅ CREAR PEDIDO DESDE CARRITO
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<Order> createOrder(@RequestParam Long userId,
                                             @RequestParam String direccionEnvio,
                                             @RequestParam String metodoPago) {
        Order savedOrder = orderService.createOrderFromCart(userId, direccionEnvio, metodoPago);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }

    // ✅ OBTENER PEDIDOS POR USUARIO
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getOrdersByUser(@PathVariable Long userId) {
        List<Order> orders = orderService.findByUser(userId);
        return ResponseEntity.ok(orders);
    }

    // ✅ OBTENER TODOS LOS PEDIDOS (ADMIN)
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.findAll();
        return ResponseEntity.ok(orders);
    }

    // ✅ OBTENER PEDIDO POR ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.findById(id);
        return ResponseEntity.ok(order);
    }

    // ✅ ACTUALIZAR ESTADO DEL PEDIDO (ADMIN)
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestParam String estado) {
        Order updatedOrder = orderService.updateOrderStatus(id, estado);
        return ResponseEntity.ok(updatedOrder);
    }

    // ✅ CANCELAR PEDIDO (CLIENTE O ADMIN)
    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long id) {
        Order cancelledOrder = orderService.cancelOrder(id);
        return ResponseEntity.ok(cancelledOrder);
    }

    // ✅ ELIMINAR PEDIDO (ADMIN - solo pendiente)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}