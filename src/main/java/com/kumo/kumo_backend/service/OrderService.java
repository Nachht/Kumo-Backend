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

    // ===== LISTAR TODOS LOS PEDIDOS CON USUARIO =====
    @Transactional(readOnly = true)
    public List<Order> findAll() {
        // 🔥 USAR JOIN FETCH PARA CARGAR EL USUARIO
        return orderRepository.findAllWithUser();
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
        System.out.println("🛒 ===== PROCESANDO COMPRA =====");
        System.out.println("👤 Usuario ID: " + userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        Cart cart = cartRepository.findByUserIdAndEstado(userId, "activo")
                .orElseThrow(() -> new RuntimeException("No hay carrito activo para el usuario"));

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        System.out.println("📦 Total de items en carrito: " + cart.getCartItems().size());

        Order order = new Order(user, direccionEnvio, metodoPago);

        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();
            int cantidadComprada = cartItem.getCantidad();
            int stockActual = product.getStock();
            int nuevoStock = stockActual - cantidadComprada;

            System.out.println("📦 Producto: " + product.getNombre());
            System.out.println("   📊 Stock actual: " + stockActual);
            System.out.println("   🛒 Cantidad comprada: " + cantidadComprada);
            System.out.println("   📉 Nuevo stock: " + nuevoStock);

            // Validar stock
            if (product.getStock() < cantidadComprada) {
                throw new RuntimeException("Stock insuficiente para: " + product.getNombre() +
                        ". Disponible: " + product.getStock() + ", Solicitado: " + cantidadComprada);
            }

            // Crear item del pedido
            OrderItem orderItem = new OrderItem(
                    cantidadComprada,
                    cartItem.getPrecioUnitario(),
                    order,
                    product
            );
            order.addOrderItem(orderItem);

            // 🔥 ACTUALIZAR STOCK
            product.setStock(nuevoStock);
            productRepository.save(product);

            // 🔥 SI EL STOCK LLEGA A 0, DESACTIVAR AUTOMÁTICAMENTE
            if (nuevoStock <= 0) {
                product.setActivo(false);
                product.setStock(0);
                productRepository.save(product);
                System.out.println("🔴 Producto '" + product.getNombre() + "' AGOTADO y desactivado");

                // 🔥 OPCIONAL: Disparar evento o notificación (si tienes implementado)
                // applicationEventPublisher.publishEvent(new ProductAgotadoEvent(product));
            }
        }

        // Marcar carrito como finalizado
        cart.setEstado("finalizado");
        cartRepository.save(cart);

        System.out.println("✅ Compra completada exitosamente. Pedido ID: " + order.getId());

        return orderRepository.save(order);
    }

    // ===== ACTUALIZAR ESTADO DEL PEDIDO =====
    @Transactional
    public Order updateOrderStatus(Long orderId, String nuevoEstado) {
        Order order = findById(orderId);

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

        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getCantidad());
            productRepository.save(product);
        }

        order.setEstado("cancelado");
        return orderRepository.save(order);
    }

    // ===== ELIMINAR PEDIDO =====
    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = findById(orderId);

        if (!order.getEstado().equals("pendiente")) {
            throw new RuntimeException("Solo se pueden eliminar pedidos en estado pendiente");
        }

        orderRepository.deleteById(orderId);
    }
}