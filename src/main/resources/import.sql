-- Categories
INSERT INTO categories(name) VALUES ('Iluminacion');
INSERT INTO categories(name) VALUES ('Cables y Accesorios');
INSERT INTO categories(name) VALUES ('Herramientas Electricas');
INSERT INTO categories(name) VALUES ('Automatizacion y Domotica');
INSERT INTO categories(name) VALUES ('Seguridad Electrica');

-- Products
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Foco LED 10W', 'Iluminación LED eficiente','Foco LED de 10W ideal para interiores, luz blanca fría, durabilidad de 15,000 horas.', 50, '5x5x10 cm', 1, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Foco LED 20W', 'Foco potente para exteriores', 'Luz LED de 20W para exteriores, resistente al agua y de bajo consumo.', 90, '6x6x12 cm', 1, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Panel Solar 100W', 'Panel solar compacto', 'Generador solar portátil de 100W para zonas sin red eléctrica o respaldo de emergencia.', 800, '100x60x3 cm', 1, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Regleta 4 enchufes', 'Regleta con protección', 'Regleta con interruptor y protección contra sobretensiones, ideal para oficina.', 40, '30x5x4 cm', 3, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Interruptor WiFi', 'Control desde tu celular', 'Interruptor inteligente WiFi compatible con Alexa y Google Home.', 60, '10x6x3 cm', 4, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Cable Coaxial 10m', 'Cable resistente', 'Cable coaxial RG6 ideal para cámaras y TV. Blindado, 10 metros de longitud.', 30, '1000x0.5x0.5 cm', 2, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Cable UTP Cat6 20m', 'Cable de red rápido', 'Cable de red UTP categoría 6, ideal para conexión de alta velocidad hasta 1Gbps.', 35, '2000x0.5x0.5 cm', 2, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Taladro 500W', 'Herramienta para perforar', 'Taladro eléctrico de 500W con control de velocidad, mango ergonómico y brocas.', 150, '30x20x10 cm', 3, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Multímetro Digital', 'Medición eléctrica precisa', 'Multímetro digital con medición de voltaje, corriente y continuidad, pantalla retroiluminada.', 70, '15x8x4 cm', 3, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Sensor de movimiento WiFi', 'Automatiza con sensores', 'Sensor WiFi de movimiento con notificaciones y compatibilidad con apps móviles.', 85, '8x6x3 cm', 4, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Cámara de Seguridad IP', 'Vigilancia en tiempo real', 'Cámara IP HD con visión nocturna, detección de movimiento y acceso desde app móvil.', 120, '12x8x6 cm', 5, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Alarma contra incendios', 'Seguridad ante incendios', 'Alarma con sensor de humo y calor, alimentación a batería, ideal para hogares y oficinas.', 45, '10x10x4 cm', 5, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Caja de herramientas eléctrica', 'Todo en uno eléctrico', 'Caja con destornilladores, pelacables, multímetro, cinta aislante y más.', 150, '40x30x10 cm', 3, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Timbre Inteligente WiFi', 'Notificaciones en tu celular', 'Timbre con video HD, micrófono bidireccional y conectividad WiFi.', 95, '15x7x4 cm', 4, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Interruptor Termomagnético 32A', 'Protección contra sobrecargas', 'Disyuntor de 32A para proteger instalaciones domésticas o industriales.', 25, '10x4x3 cm', 5, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Reflector LED 50W', 'Iluminación potente exterior', 'Reflector de aluminio, IP65 resistente al agua, 50W.', 100, '20x20x5 cm', 1, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Sensor de apertura WiFi', 'Monitorea puertas y ventanas', 'Sensor magnético WiFi compatible con domótica, con batería de larga duración.', 55, '6x3x1 cm', 4, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Canaleta plástica 2m', 'Ordena cables fácilmente', 'Canaleta blanca de PVC de 2 metros para ocultar y ordenar cables.', 20, '200x2x1 cm', 2, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Lámpara LED con sensor', 'Se enciende automáticamente', 'Lámpara LED con sensor de movimiento para pasillos o entradas.', 65, '18x12x5 cm', 1, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Controlador de persianas WiFi', 'Automatiza tus cortinas', 'Controlador inteligente para persianas eléctricas, compatible con asistentes virtuales.', 80, '10x6x3 cm', 4, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Detector de gas', 'Prevención de fugas', 'Detector de gas metano/GLP con alarma sonora, ideal para cocinas.', 49, '12x8x3 cm', 5, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Cable de cobre 2x1.5mm 50m', 'Conducción eficiente', 'Rollo de cable dúplex de cobre para instalaciones eléctricas residenciales.', 110, '50x5x5 cm', 2, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Fuente de poder 12V 5A', 'Alimenta tus dispositivos', 'Fuente para cámaras, luces LED o routers, entrada AC y salida estable.', 35, '12x6x4 cm', 2, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Tomacorriente doble', 'Conexión segura', 'Tomacorriente empotrable doble con puesta a tierra.', 18, '8x8x5 cm', 2, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Sensor crepuscular', 'Se enciende al anochecer', 'Sensor que activa iluminación automáticamente según luz ambiente.', 33, '5x5x3 cm', 4, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Lámpara colgante industrial', 'Estilo moderno', 'Lámpara colgante metálica estilo vintage, ideal para cocina o comedor.', 130, '25x25x25 cm', 1, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Termostato inteligente', 'Controla la temperatura', 'Termostato WiFi con programación horaria y control desde app.', 160, '14x10x3 cm', 4, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Cámara domo IR 2MP', 'Vigilancia 360°', 'Cámara domo con visión infrarroja y resolución HD para interior.', 89, '10x10x10 cm', 5, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Caja estanca IP65', 'Protección al aire libre', 'Caja de conexiones eléctrica con protección contra agua y polvo.', 42, '15x10x8 cm', 5, 0);
INSERT INTO products (name, short_description, long_description, price, dimensions, category_id, views) VALUES ('Kit de automatización', 'Tu hogar inteligente', 'Kit con sensor, enchufe inteligente, interruptor y app de control.', 200, '30x20x10 cm', 4, 0);

-- Users
INSERT INTO users (email, password, first_name, last_name, city, address, phone_number) VALUES ('juan@ejemplo.com', 'pass1234', 'Juan', 'Pérez', 'Ciudad Luz', 'Calle Falsa 123', '555-1111');
INSERT INTO users (email, password, first_name, last_name, city, address, phone_number) VALUES ('ana@ejemplo.com', 'clave5678', 'Ana', 'García', 'Ciudad Corriente', 'Av. Voltaje 456', '555-2222');

-- Orders
INSERT INTO orders (total_price, date, first_name, last_name, city, address, email, phone_number, user_id) VALUES (230.00, CURRENT_TIMESTAMP, 'Juan', 'Pérez', 'Ciudad Luz', 'Calle Falsa 123', 'juan@ejemplo.com', '555-1111', 1);
INSERT INTO orders (total_price, date, first_name, last_name, city, address, email, phone_number, user_id) VALUES (160.00, CURRENT_TIMESTAMP, 'Ana', 'García', 'Ciudad Corriente', 'Av. Voltaje 456', 'ana@ejemplo.com', '555-2222', 2);
INSERT INTO orders (total_price, date, first_name, last_name, city, address, email, phone_number, user_id) VALUES (230.00, CURRENT_TIMESTAMP, 'Juan', 'Pérez', 'Ciudad Luz', 'Calle Falsa 123', 'juan@ejemplo.com', '555-1111', 2);

-- Order-Product Relationships
INSERT INTO orders_products (order_id, product_id) VALUES (1, 1);
INSERT INTO orders_products (order_id, product_id) VALUES (1, 4);
INSERT INTO orders_products (order_id, product_id) VALUES (1, 6);
INSERT INTO orders_products (order_id, product_id) VALUES (2, 2);
INSERT INTO orders_products (order_id, product_id) VALUES (2, 5);
INSERT INTO orders_products (order_id, product_id) VALUES (2, 7);

-- User-Role Relationships (commented out)
-- insert into user_role (user_id, roles) values (1, 'ADMIN');
-- insert into user_role (user_id, roles) values (2, 'USER');