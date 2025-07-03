# Documento de Requisitos Completo - Total Electro

## 1. Introducción

### 1.1 Propósito del Documento
Este documento describe los requisitos funcionales y no funcionales del sistema Total Electro, una plataforma de e-commerce especializada en productos eléctricos y automatización del hogar.

### 1.2 Alcance del Sistema
El sistema Total Electro abarca la gestión completa de un negocio de e-commerce, incluyendo:
- Gestión de usuarios y autenticación
- Catálogo de productos y categorías
- Carrito de compras y proceso de checkout
- Gestión de pedidos y envíos
- Sistema de preguntas y respuestas
- Sistema de reseñas y calificaciones
- Panel de administración
- Generación de reportes

### 1.3 Stakeholders
- **Clientes finales**: Usuarios que compran productos
- **Administradores**: Personal que gestiona el sistema
- **Desarrolladores**: Equipo de desarrollo y mantenimiento

## 2. Requisitos Funcionales

### 2.1 Gestión de Usuarios

#### RF001 - Registro de Usuario
- **Descripción**: El sistema debe permitir que nuevos usuarios se registren
- **Prioridad**: Alta
- **Criterios de Aceptación**:
  - El usuario debe poder ingresar nombre, apellido, email, contraseña, ciudad, dirección y teléfono
  - El email debe ser único en el sistema
  - La contraseña debe ser encriptada con BCrypt
  - Se debe asignar automáticamente el rol "USER"
  - Se debe validar que todos los campos sean obligatorios

#### RF002 - Autenticación de Usuario
- **Descripción**: El sistema debe permitir que usuarios registrados inicien sesión
- **Prioridad**: Alta
- **Criterios de Aceptación**:
  - El usuario debe poder iniciar sesión con email y contraseña
  - El sistema debe validar las credenciales contra la base de datos
  - Se debe crear una sesión segura para el usuario autenticado
  - Se debe redirigir al usuario a la página principal después del login exitoso

#### RF003 - Gestión de Perfil
- **Descripción**: Los usuarios deben poder actualizar su información personal
- **Prioridad**: Media
- **Criterios de Aceptación**:
  - El usuario debe poder modificar nombre, apellido, email, ciudad, dirección y teléfono
  - Se debe validar que el nuevo email no esté en uso por otro usuario
  - El usuario debe poder cambiar su contraseña

### 2.2 Gestión de Productos

#### RF004 - Catálogo de Productos
- **Descripción**: El sistema debe mostrar un catálogo de productos organizados por categorías
- **Prioridad**: Alta
- **Criterios de Aceptación**:
  - Los productos deben estar organizados en categorías (Iluminación, Cables y Accesorios, Herramientas Eléctricas, etc.)
  - Cada producto debe mostrar nombre, descripción corta, precio, imagen y descuento si aplica
  - Se debe implementar paginación para grandes cantidades de productos
  - Los productos deben mostrar su popularidad basada en vistas

#### RF005 - Búsqueda y Filtros
- **Descripción**: Los usuarios deben poder buscar productos y aplicar filtros
- **Prioridad**: Alta
- **Criterios de Aceptación**:
  - Búsqueda por nombre de producto
  - Filtros por rango de precio
  - Filtros por categoría
  - Ordenamiento por relevancia, precio (ascendente/descendente), nombre (ascendente/descendente)

#### RF006 - Detalles de Producto
- **Descripción**: Los usuarios deben poder ver información detallada de cada producto
- **Prioridad**: Alta
- **Criterios de Aceptación**:
  - Mostrar información completa del producto (nombre, descripción larga, precio, dimensiones, imagen)
  - Mostrar preguntas y respuestas del producto
  - Mostrar reseñas y calificaciones
  - Contador de vistas del producto
  - Botón para agregar al carrito

### 2.3 Carrito de Compras

#### RF007 - Gestión del Carrito
- **Descripción**: Los usuarios deben poder gestionar productos en su carrito de compras
- **Prioridad**: Alta
- **Criterios de Aceptación**:
  - Agregar productos al carrito con cantidad específica
  - Actualizar cantidades de productos en el carrito
  - Remover productos del carrito
  - Limpiar todo el carrito
  - Mostrar subtotal del carrito
  - Persistir el carrito en la base de datos

#### RF008 - Cálculo de Envío
- **Descripción**: El sistema debe calcular el costo de envío basado en el CEP del cliente
- **Prioridad**: Alta
- **Criterios de Aceptación**:
  - Calcular envío basado en distancia entre CEP de origen y destino
  - Aplicar tarifa base + tarifa por kilómetro
  - Envío gratuito para compras superiores a $150
  - Límite máximo de distancia para envío (100km)
  - Mostrar tiempo estimado de entrega

#### RF009 - Aplicación de Cupones
- **Descripción**: El sistema debe permitir aplicar cupones de descuento
- **Prioridad**: Media
- **Criterios de Aceptación**:
  - Cupón "LUZ15" para 15% de descuento en productos de iluminación
  - Validar que el cupón sea válido
  - Aplicar descuento al subtotal
  - Mostrar descuento aplicado en el carrito

### 2.4 Proceso de Checkout

#### RF010 - Proceso de Compra
- **Descripción**: Los usuarios deben poder completar la compra de productos
- **Prioridad**: Alta
- **Criterios de Aceptación**:
  - Validar información de envío (nombre, dirección, teléfono, email)
  - Validar formato de CEP
  - Calcular total final (subtotal + envío + impuestos)
  - Crear pedido en estado "PENDIENTE"
  - Limpiar carrito después de la compra exitosa
  - Redirigir a confirmación de pedido

### 2.5 Gestión de Pedidos

#### RF011 - Seguimiento de Pedidos
- **Descripción**: Los usuarios deben poder ver el historial de sus pedidos
- **Prioridad**: Media
- **Criterios de Aceptación**:
  - Mostrar lista de pedidos del usuario
  - Mostrar estado del pedido (PENDIENTE, COMPLETADA, CANCELADA)
  - Mostrar fecha, total y productos del pedido
  - Ordenar por fecha más reciente

#### RF012 - Gestión Administrativa de Pedidos
- **Descripción**: Los administradores deben poder gestionar pedidos
- **Prioridad**: Alta
- **Criterios de Aceptación**:
  - Ver todos los pedidos del sistema
  - Cambiar estado de pedidos (completar/cancelar)
  - Ver detalles completos de cada pedido
  - Filtrar pedidos por estado

### 2.6 Sistema de Preguntas y Respuestas

#### RF013 - Preguntas sobre Productos
- **Descripción**: Los usuarios deben poder hacer preguntas sobre productos
- **Prioridad**: Media
- **Criterios de Aceptación**:
  - Usuarios autenticados pueden hacer preguntas
  - Las preguntas se muestran en la página del producto
  - Mostrar fecha de la pregunta y nombre del usuario
  - Ordenar preguntas por fecha

#### RF014 - Respuestas a Preguntas
- **Descripción**: Los administradores deben poder responder preguntas
- **Prioridad**: Media
- **Criterios de Aceptación**:
  - Solo administradores pueden responder preguntas
  - Las respuestas se muestran junto a las preguntas
  - Mostrar fecha de respuesta y nombre del administrador
  - Marcar pregunta como respondida

### 2.7 Sistema de Reseñas

#### RF015 - Reseñas de Productos
- **Descripción**: Los usuarios deben poder dejar reseñas de productos comprados
- **Prioridad**: Media
- **Criterios de Aceptación**:
  - Solo usuarios que han comprado el producto pueden reseñar
  - Calificación de 1 a 5 estrellas
  - Comentario opcional
  - Posibilidad de adjuntar fotos
  - Mostrar reseñas en la página del producto

### 2.8 Panel de Administración

#### RF016 - Dashboard Administrativo
- **Descripción**: Los administradores deben tener acceso a un panel de control
- **Prioridad**: Alta
- **Criterios de Aceptación**:
  - Mostrar estadísticas generales (total de pedidos, productos, usuarios)
  - Mostrar ventas del mes actual
  - Lista de pedidos recientes
  - Acceso rápido a funciones administrativas

#### RF017 - Gestión de Productos (Admin)
- **Descripción**: Los administradores deben poder gestionar productos
- **Prioridad**: Alta
- **Criterios de Aceptación**:
  - Crear nuevos productos
  - Editar productos existentes
  - Eliminar productos
  - Asignar categorías
  - Definir precios y descuentos
  - Subir imágenes de productos

#### RF018 - Gestión de Usuarios (Admin)
- **Descripción**: Los administradores deben poder gestionar usuarios
- **Prioridad**: Media
- **Criterios de Aceptación**:
  - Ver lista de todos los usuarios
  - Ver detalles de usuarios
  - Editar información de usuarios
  - Eliminar usuarios (solo si no tienen pedidos)
  - Asignar/quitar roles

#### RF019 - Gestión de Categorías
- **Descripción**: Los administradores deben poder gestionar categorías de productos
- **Prioridad**: Media
- **Criterios de Aceptación**:
  - Crear nuevas categorías
  - Editar categorías existentes
  - Eliminar categorías (solo si no tienen productos)
  - Definir slug para URLs amigables

### 2.9 Sistema de Reportes

#### RF020 - Generación de Reportes
- **Descripción**: El sistema debe generar reportes en PDF
- **Prioridad**: Media
- **Criterios de Aceptación**:
  - Reporte de ventas por período
  - Reporte de productos más vendidos
  - Reporte de usuarios registrados
  - Exportar reportes en formato PDF
  - Incluir gráficos y estadísticas

## 3. Requisitos No Funcionales

### 3.1 Rendimiento
- **RNF001**: El sistema debe responder en menos de 3 segundos para operaciones normales
- **RNF002**: El sistema debe soportar al menos 100 usuarios concurrentes
- **RNF003**: La base de datos debe optimizarse para consultas frecuentes

### 3.2 Seguridad
- **RNF004**: Todas las contraseñas deben estar encriptadas con BCrypt
- **RNF005**: Implementar autenticación basada en roles (USER, ADMIN)
- **RNF006**: Protección contra ataques CSRF
- **RNF007**: Validación de entrada en todos los formularios
- **RNF008**: Sesiones seguras con timeout automático

### 3.3 Usabilidad
- **RNF009**: Interfaz responsiva que funcione en dispositivos móviles
- **RNF010**: Navegación intuitiva y fácil de usar
- **RNF011**: Mensajes de error claros y útiles
- **RNF012**: Carga progresiva de contenido para mejor experiencia

### 3.4 Compatibilidad
- **RNF013**: Compatible con navegadores modernos (Chrome, Firefox, Safari, Edge)
- **RNF014**: Funcionamiento en sistemas operativos Windows, macOS y Linux
- **RNF015**: Soporte para Java 17 o superior

### 3.5 Mantenibilidad
- **RNF016**: Código bien documentado y estructurado
- **RNF017**: Uso de patrones de diseño apropiados
- **RNF018**: Separación clara de responsabilidades (MVC)
- **RNF019**: Logs detallados para debugging

### 3.6 Escalabilidad
- **RNF020**: Arquitectura que permita agregar nuevas funcionalidades fácilmente
- **RNF021**: Base de datos diseñada para crecimiento futuro
- **RNF022**: Código modular y reutilizable

## 4. Requisitos de Interfaz

### 4.1 Interfaz de Usuario
- Diseño moderno y profesional
- Paleta de colores consistente (naranja #f36f21 como color principal)
- Tipografía Roboto para mejor legibilidad
- Iconos de Material Design
- Animaciones suaves para mejor UX

### 4.2 Interfaz de Administración
- Dashboard con estadísticas visuales
- Tablas responsivas para datos
- Formularios intuitivos
- Confirmaciones para acciones críticas
- Navegación clara entre secciones

## 5. Restricciones del Sistema

### 5.1 Técnicas
- Debe desarrollarse en Java con Spring Boot
- Base de datos H2 para desarrollo
- Frontend con Thymeleaf y Bootstrap
- Reportes con JasperReports

### 5.2 Operacionales
- Sistema debe estar disponible 24/7
- Backup automático de base de datos
- Monitoreo de errores y performance

### 5.3 Legales
- Cumplimiento con LGPD (Lei Geral de Proteção de Dados)
- Términos de servicio claros
- Política de privacidad

## 6. Casos de Uso Principales

### 6.1 Compra de Productos
1. Usuario navega por categorías o busca productos
2. Selecciona productos y los agrega al carrito
3. Calcula envío ingresando su CEP
4. Aplica cupón de descuento si tiene
5. Completa checkout con información personal
6. Confirma compra y recibe confirmación

### 6.2 Gestión Administrativa
1. Administrador accede al panel de control
2. Revisa estadísticas y pedidos pendientes
3. Gestiona productos (crear, editar, eliminar)
4. Responde preguntas de usuarios
5. Genera reportes de ventas
6. Gestiona usuarios del sistema

## 7. Glosario

- **CEP**: Código de Endereçamento Postal (código postal brasileño)
- **Checkout**: Proceso final de compra
- **Cupón**: Código de descuento aplicable a productos
- **Dashboard**: Panel de control administrativo
- **Slug**: Versión URL-friendly del nombre de una categoría
- **Subtotal**: Suma de productos antes de impuestos y envío 