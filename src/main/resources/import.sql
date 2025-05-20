-- Categories
INSERT INTO categories(name) VALUES ('Iluminacion');
INSERT INTO categories(name) VALUES ('Cables y Accesorios');
INSERT INTO categories(name) VALUES ('Herramientas Electricas');
INSERT INTO categories(name) VALUES ('Automatizacion y Domotica');
INSERT INTO categories(name) VALUES ('Seguridad Electrica');

-- Products
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Foco LED 10W', 'Iluminación LED eficiente', 'Foco LED de 10W ideal para interiores, luz blanca fría, durabilidad de 15,000 horas.', 50, '5x5x10 cm', 1, 0, '/images/products/foco-led-10w.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Foco LED 20W', 'Foco potente para exteriores', 'Luz LED de 20W para exteriores, resistente al agua y de bajo consumo.', 90, '6x6x12 cm', 1, 0, '/images/products/foco-led-20w.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Panel Solar 100W', 'Panel solar compacto', 'Generador solar portátil de 100W para zonas sin red eléctrica o respaldo de emergencia.', 800, '100x60x3 cm', 1, 0, '/images/products/panel-solar-100w.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Regleta 4 enchufes', 'Regleta con protección', 'Regleta con interruptor y protección contra sobretensiones, ideal para oficina.', 40, '30x5x4 cm', 3, 0, '/images/products/regleta-4-enchufes.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Interruptor WiFi', 'Control desde tu celular', 'Interruptor inteligente WiFi compatible con Alexa y Google Home.', 60, '10x6x3 cm', 4, 0, '/images/products/interruptor-wifi.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Cable Coaxial 10m', 'Cable resistente', 'Cable coaxial RG6 ideal para cámaras y TV. Blindado, 10 metros de longitud.', 30, '1000x0.5x0.5 cm', 2, 0, '/images/products/cable-coaxial-10m.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Cable UTP Cat6 20m', 'Cable de red rápido', 'Cable de red UTP categoría 6, ideal para conexión de alta velocidad hasta 1Gbps.', 35, '2000x0.5x0.5 cm', 2, 0, '/images/products/cable-utp-cat6-20m.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Taladro 500W', 'Herramienta para perforar', 'Taladro eléctrico de 500W con control de velocidad, mango ergonómico y brocas.', 150, '30x20x10 cm', 3, 0, '/images/products/taladro-500w.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Multímetro Digital', 'Medición eléctrica precisa', 'Multímetro digital con medición de voltaje, corriente y continuidad, pantalla retroiluminada.', 70, '15x8x4 cm', 3, 0, '/images/products/multimetro-digital.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Sensor de movimiento WiFi', 'Automatiza con sensores', 'Sensor WiFi de movimiento con notificaciones y compatibilidad con apps móviles.', 85, '8x6x3 cm', 4, 0, '/images/products/sensor-movimiento-wifi.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Cámara de Seguridad IP', 'Vigilancia en tiempo real', 'Cámara IP HD con visión nocturna, detección de movimiento y acceso desde app móvil.', 120, '12x8x6 cm', 5, 0, '/images/products/camara-seguridad-ip.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Alarma contra incendios', 'Seguridad ante incendios', 'Alarma con sensor de humo y calor, alimentación a batería, ideal para hogares y oficinas.', 45, '10x10x4 cm', 5, 0, '/images/products/alarma-incendios.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Caja de herramientas eléctrica', 'Todo en uno eléctrico', 'Caja con destornilladores, pelacables, multímetro, cinta aislante y más.', 150, '40x30x10 cm', 3, 0, '/images/products/caja-herramientas.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Timbre Inteligente WiFi', 'Notificaciones en tu celular', 'Timbre con video HD, micrófono bidireccional y conectividad WiFi.', 95, '15x7x4 cm', 4, 0, '/images/products/timbre-inteligente.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Interruptor Termomagnético 32A', 'Protección contra sobrecargas', 'Disyuntor de 32A para proteger instalaciones domésticas o industriales.', 25, '10x4x3 cm', 5, 0, '/images/products/interruptor-termomagnetico.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Reflector LED 50W', 'Iluminación potente exterior', 'Reflector de aluminio, IP65 resistente al agua, 50W.', 100, '20x20x5 cm', 1, 0, '/images/products/reflector-led-50w.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Sensor de apertura WiFi', 'Monitorea puertas y ventanas', 'Sensor magnético WiFi compatible con domótica, con batería de larga duración.', 55, '6x3x1 cm', 4, 0, '/images/products/sensor-apertura-wifi.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Canaleta plástica 2m', 'Ordena cables fácilmente', 'Canaleta blanca de PVC de 2 metros para ocultar y ordenar cables.', 20, '200x2x1 cm', 2, 0, '/images/products/canaleta-plastica.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Lámpara LED con sensor', 'Se enciende automáticamente', 'Lámpara LED con sensor de movimiento para pasillos o entradas.', 65, '18x12x5 cm', 1, 0, '/images/products/lampara-led-sensor.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Controlador de persianas WiFi', 'Automatiza tus cortinas', 'Controlador inteligente para persianas eléctricas, compatible con asistentes virtuales.', 80, '10x6x3 cm', 4, 0, '/images/products/controlador-persianas.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Detector de gas', 'Prevención de fugas', 'Detector de gas metano/GLP con alarma sonora, ideal para cocinas.', 49, '12x8x3 cm', 5, 0, '/images/products/detector-gas.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Cable de cobre 2x1.5mm 50m', 'Conducción eficiente', 'Rollo de cable dúplex de cobre para instalaciones eléctricas residenciales.', 110, '50x5x5 cm', 2, 0, '/images/products/cable-cobre.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Fuente de poder 12V 5A', 'Alimenta tus dispositivos', 'Fuente para cámaras, luces LED o routers, entrada AC y salida estable.', 35, '12x6x4 cm', 2, 0, '/images/products/fuente-poder.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Tomacorriente doble', 'Conexión segura', 'Tomacorriente empotrable doble con puesta a tierra.', 18, '8x8x5 cm', 2, 0, '/images/products/tomacorriente-doble.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Sensor crepuscular', 'Se enciende al anochecer', 'Sensor que activa iluminación automáticamente según luz ambiente.', 33, '5x5x3 cm', 4, 0, '/images/products/sensor-crepuscular.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Lámpara colgante industrial', 'Estilo moderno', 'Lámpara colgante metálica estilo vintage, ideal para cocina o comedor.', 130, '25x25x25 cm', 1, 0, '/images/products/lampara-colgante.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Termostato inteligente', 'Controla la temperatura', 'Termostato WiFi con programación horaria y control desde app.', 160, '14x10x3 cm', 4, 0, '/images/products/termostato-inteligente.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Cámara domo IR 2MP', 'Vigilancia 360°', 'Cámara domo con visión infrarroja y resolución HD para interior.', 89, '10x10x10 cm', 5, 0, '/images/products/camara-domo.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Caja estanca IP65', 'Protección al aire libre', 'Caja de conexiones eléctrica con protección contra agua y polvo.', 42, '15x10x8 cm', 5, 0, '/images/products/caja-estanca.jpg');
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views, image_url) VALUES ('Kit de automatización', 'Tu hogar inteligente', 'Kit con sensor, enchufe inteligente, interruptor y app de control.', 200, '30x20x10 cm', 4, 0, '/images/products/kit-automatizacion.jpg');

-- Roles
INSERT INTO roles (name) VALUES ('USER');
INSERT INTO roles (name) VALUES ('ADMIN');

-- Users (con contraseñas encriptadas)
-- Contraseña para juan@ejemplo.com: pass1234
-- Contraseña para ana@ejemplo.com: clave5678
-- Contraseña para admin@totalelectro.com: pass1234
INSERT INTO users (email, password, first_name, last_name, city, address, phone_number) VALUES ('juan@ejemplo.com', '$2a$10$CEBO1m5bHlhJhLOeNh06mO.HaGwaIkDZw6ZMBg0ovgGYOyQN1ivlm', 'Juan', 'Pérez', 'Ciudad Luz', 'Calle Falsa 123', '555-1111');
INSERT INTO users (email, password, first_name, last_name, city, address, phone_number) VALUES ('ana@ejemplo.com', '$2a$10$8K1p/a0dR1LXMIgoEDFrwOfgqw7z6aP3y9T0V9QUIz2VhQv5Uq6.q', 'Ana', 'García', 'Ciudad Corriente', 'Av. Voltaje 456', '555-2222');
INSERT INTO users (email, password, first_name, last_name, city, address, phone_number) VALUES ('admin@totalelectro.com', '$2a$10$CEBO1m5bHlhJhLOeNh06mO.HaGwaIkDZw6ZMBg0ovgGYOyQN1ivlm', 'Admin', 'System', 'Ciudad', 'Dirección', '123456789');

-- Asignación de roles
INSERT INTO user_roles (user_id, role_id) SELECT u.id, r.id FROM users u, roles r WHERE u.email = 'juan@ejemplo.com' AND r.name = 'ADMIN';
INSERT INTO user_roles (user_id, role_id) SELECT u.id, r.id FROM users u, roles r WHERE u.email = 'ana@ejemplo.com' AND r.name = 'USER';
INSERT INTO user_roles (user_id, role_id) SELECT u.id, r.id FROM users u, roles r WHERE u.email = 'admin@totalelectro.com' AND r.name = 'ADMIN';

-- Orders
INSERT INTO orders (total_price, date, status, first_name, last_name, city, address, email, phone_number, user_id) VALUES (230.00, CURRENT_TIMESTAMP, 'PENDIENTE', 'Juan', 'Pérez', 'Ciudad Luz', 'Calle Falsa 123', 'juan@ejemplo.com', '555-1111', 1);
INSERT INTO orders (total_price, date, status, first_name, last_name, city, address, email, phone_number, user_id) VALUES (160.00, CURRENT_TIMESTAMP, 'COMPLETADA', 'Ana', 'García', 'Ciudad Corriente', 'Av. Voltaje 456', 'ana@ejemplo.com', '555-2222', 2);
INSERT INTO orders (total_price, date, status, first_name, last_name, city, address, email, phone_number, user_id) VALUES (230.00, CURRENT_TIMESTAMP, 'PENDIENTE', 'Juan', 'Pérez', 'Ciudad Luz', 'Calle Falsa 123', 'juan@ejemplo.com', '555-1111', 2);

-- Order-Product Relationships
INSERT INTO orders_products (order_id, product_id) VALUES (1, 1);
INSERT INTO orders_products (order_id, product_id) VALUES (1, 4);
INSERT INTO orders_products (order_id, product_id) VALUES (1, 6);
INSERT INTO orders_products (order_id, product_id) VALUES (2, 2);
INSERT INTO orders_products (order_id, product_id) VALUES (2, 5);
INSERT INTO orders_products (order_id, product_id) VALUES (2, 7);