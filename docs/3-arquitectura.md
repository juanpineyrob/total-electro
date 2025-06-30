# Documentación de la Arquitectura

## Estructura General

- **Backend:** Java 17, Spring Boot, Spring Data JPA, Spring Security, JasperReports.
- **Frontend:** Thymeleaf, Bootstrap, HTML/CSS.
- **Base de datos:** H2 (desarrollo), scripts SQL para inicialización.
- **Seguridad:** Roles USER y ADMIN, autenticación con email y contraseña.

## Diagrama E-R (Mermaid)

```mermaid
erDiagram
    USERS ||--o{ USER_ROLES : ""
    ROLES ||--o{ USER_ROLES : ""
    USERS ||--o{ ORDERS : ""
    ORDERS ||--o{ ORDERS_PRODUCTS : ""
    PRODUCTS ||--o{ ORDERS_PRODUCTS : ""
    PRODUCTS ||--o{ PRODUCT_QUESTIONS : ""
    USERS ||--o{ PRODUCT_QUESTIONS : ""
    PRODUCTS ||--o{ PRODUCT_REVIEWS : ""
    USERS ||--o{ PRODUCT_REVIEWS : ""
    PRODUCT_REVIEWS ||--o{ PRODUCT_REVIEW_PHOTOS : ""
    CATEGORIES ||--o{ PRODUCTS : ""

    USERS {
      bigint id
      string email
      string password
      string first_name
      string last_name
      string city
      string address
      string phone_number
    }
    ROLES {
      bigint id
      string name
    }
    PRODUCTS {
      bigint id
      string name
      string short_description
      string long_description
      decimal price
      string dimensions
      bigint category_id
      int views
      string image_url
      double discount_percent
    }
    CATEGORIES {
      bigint id
      string name
      string slug
    }
    ORDERS {
      bigint id
      double total_price
      datetime date
      string status
      string first_name
      string last_name
      string city
      string address
      string email
      string phone_number
      bigint user_id
    }
    PRODUCT_QUESTIONS {
      bigint id
      bigint product_id
      bigint user_id
      string question
      string answer
      bigint answered_by
      datetime answered_at
      datetime created_at
      boolean is_answered
    }
    PRODUCT_REVIEWS {
      bigint id
      bigint product_id
      bigint user_id
      bigint order_id
      int rating
      string comment
      datetime created_at
    }
    PRODUCT_REVIEW_PHOTOS {
      bigint id
      bigint review_id
      string url
    }
}
```

## Diagrama de Casos de Uso (Mermaid)

```mermaid
usecaseDiagram
  actor Usuario
  actor Admin
  Usuario --> (Registrarse)
  Usuario --> (Iniciar sesión)
  Usuario --> (Buscar productos)
  Usuario --> (Agregar al carrito)
  Usuario --> (Comprar)
  Usuario --> (Hacer preguntas)
  Usuario --> (Dejar reseñas)
  Admin --> (Gestionar productos)
  Admin --> (Gestionar usuarios)
  Admin --> (Gestionar pedidos)
  Admin --> (Responder preguntas)
  Admin --> (Generar reportes)
``` 