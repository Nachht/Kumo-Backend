package com.kumo.kumo_backend.service;

import com.kumo.kumo_backend.model.*;
import com.kumo.kumo_backend.repository.CartRepository;
import com.kumo.kumo_backend.repository.OrderRepository;
import com.kumo.kumo_backend.repository.ProductRepository;
import com.kumo.kumo_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository,
                        CartRepository cartRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    // ===== LISTAR TODOS LOS PEDIDOS =====
    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    // ===== BUSCAR PEDIDO POR ID =====
    @Transactional(readOnly = true)
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));
    }

    // ===== BUSCAR PEDIDOS POR USUARIO =====
    @Transactional(readOnly = true)
    public List<Order> findByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    // ===== BUSCAR PEDIDOS POR ESTADO =====
    @Transactional(readOnly = true)
    public List<Order> findByEstado(String estado) {
        return orderRepository.findByEstado(estado);
    }

    // ===== CREAR PEDIDO DESDE CARRITO =====
    @Transactional
    public Order createOrderFromCart(Long userId, String direccionEnvio, String metodoPago) {
        // 1. Verificar que el usuario existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        // 2. Obtener el carrito activo
        Cart cart = cartRepository.findByUserIdAndEstado(userId, "activo")
                .orElseThrow(() -> new RuntimeException("No hay carrito activo para el usuario"));

        // 3. Verificar que el carrito no esté vacío
        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        // 4. Crear el pedido
        Order order = new Order(user, direccionEnvio, metodoPago);

        // 5. Convertir items del carrito a items del pedido
        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();

            // Validar stock
            if (product.getStock() < cartItem.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + product.getNombre());
            }

            // Crear item del pedido
            OrderItem orderItem = new OrderItem(
                    cartItem.getCantidad(),
                    cartItem.getPrecioUnitario(),
                    order,
                    product
            );
            order.addOrderItem(orderItem);

            // Descontar stock
            product.setStock(product.getStock() - cartItem.getCantidad());
            productRepository.save(product);
        }

        // 6. Marcar el carrito como finalizado
        cart.setEstado("finalizado");
        cartRepository.save(cart);

        // 7. Guardar el pedido
        return orderRepository.save(order);
    }

    // ===== ACTUALIZAR ESTADO DEL PEDIDO =====
    @Transactional
    public Order updateOrderStatus(Long orderId, String nuevoEstado) {
        Order order = findById(orderId);

        // Validar transiciones de estado
        String estadoActual = order.getEstado();
        if (estadoActual.equals("entregado") || estadoActual.equals("cancelado")) {
            throw new RuntimeException("No se puede modificar un pedido " + estadoActual);
        }

        order.setEstado(nuevoEstado);
        return orderRepository.save(order);
    }

    // ===== CANCELAR PEDIDO =====
    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = findById(orderId);

        if (order.getEstado().equals("entregado")) {
            throw new RuntimeException("No se puede cancelar un pedido ya entregado");
        }

        // Devolver stock
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getCantidad());
            productRepository.save(product);
        }

        order.setEstado("cancelado");
        return orderRepository.save(order);
    }

    // ===== ELIMINAR PEDIDO (solo si está pendiente) =====
    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = findById(orderId);

        if (!order.getEstado().equals("pendiente")) {
            throw new RuntimeException("Solo se pueden eliminar pedidos en estado pendiente");
        }

        orderRepository.deleteById(orderId);
    }
}