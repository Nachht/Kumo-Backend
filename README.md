# 🌸 Kumo - Tu Tienda de Anime Favorita

> **Plataforma web dedicada a la venta de mangas, figuras coleccionables y merchandising oficial de anime.**

---

## 📋 Descripción

**Kumo** es una API RESTful desarrollada con **Spring Boot** que sirve como backend para una tienda online de anime y manga. La plataforma ofrece una arquitectura robusta y escalable con autenticación JWT, gestión de usuarios, productos, carrito de compras y pedidos.

---

## 🚀 Tecnologías

| Tecnología | Versión |
|------------|---------|
| **Java** | 21 |
| **Spring Boot** | 4.1.0 |
| **Spring Security** | 6.x |
| **JWT (JJWT)** | 0.12.6 |
| **MySQL** | 8.x |
| **SpringDoc OpenAPI** | 2.3.0 |
| **Gradle** | - |

---

## ✨ Características Principales

### 🔐 Autenticación y Seguridad
- Registro e inicio de sesión con JWT
- Roles: **ADMIN** y **CLIENTE**
- Contraseñas hasheadas con BCrypt
- Protección de endpoints por rol

### 👤 Gestión de Usuarios
- Visualización de perfiles (propio o por ADMIN)
- Actualización de perfil y contraseña
- Cambio de rol y estado - Solo ADMIN
- Listado de todos los usuarios - Solo ADMIN

### 📦 Gestión de Productos
- CRUD completo de productos - Solo ADMIN
- Actualización de stock - Solo ADMIN
- Búsqueda y filtrado - Público
- Visualización de productos - Público

### 🛒 Carrito de Compras
- Agregar, actualizar y eliminar productos
- Vaciar carrito completo
- Cálculo automático del total
- Carrito persistente por usuario

### 📋 Gestión de Pedidos
- Creación de pedidos desde el carrito
- Cancelación de pedidos
- Cambio de estado - Solo ADMIN
- Historial de pedidos por usuario

### 📂 Categorías
- CRUD de categorías - Solo ADMIN
- Visualización de categorías - Público

### 📧 Contacto
- Envío de mensajes - Público
- Gestión de mensajes - Solo ADMIN

---

## 📊 Estado del Proyecto

| Área | Estado |
|------|--------|
| **Backend API** | ✅ Funcional |
| **Autenticación** | ✅ Funcional |
| **Roles y Permisos** | ✅ Funcional |
| **Carrito** | ✅ Funcional |
| **Pedidos** | ✅ Funcional |
| **Productos** | ✅ Funcional |
| **Categorías** | ✅ Funcional |
| **Contacto** | ✅ Funcional |
| **Documentación** | ✅ Funcional |
| **Integración Frontend** | ✅ Funcional |

---

## 📚 Documentación con Swagger

Kumo utiliza **SpringDoc OpenAPI** para generar documentación interactiva de la API.

### 🔗 Enlaces del Proyecto

| Recurso | URL |
|---------|-----|
| **Frontend** | [https://github.com/Nachht/Kumo](https://github.com/Nachht/Kumo) |
| **Swagger UI (Local)** | `http://localhost:8081/swagger-ui/index.html` |

---

## 👥 Equipo de Desarrollo

| Nombre | GitHub |
|--------|--------|
| **Daniel Steven Casas Pulido** | [@Nachht](https://github.com/Nachht) |
| **Oscar Daniel Yustres Castro** | [@oscaryustres](https://github.com/oscaryustres) |
| **Yenny Alexandra Castro** | [@YenCastro](https://github.com/YenCastro) |
| **Yesmith Adriana Sarmiento Suarez** | [@yesmithsarmiento](https://github.com/yesmithsarmiento) |

---

## 📄 Licencia

Este proyecto está bajo la licencia **MIT**.

---

<p align="center">
  <strong>🌸 Kumo - Tu tienda de anime y manga</strong>
  
</p>
