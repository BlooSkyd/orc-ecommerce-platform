-- Données initiales pour la base de données H2
-- Ce script est exécuté automatiquement au démarrage de l'application
-- Création de la table products
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_date TIMESTAMP,
    status ENUM('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED') NOT NULL,
    total_amount DECIMAL(10,2),
    shipping_address VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2),
    subtotal DECIMAL(10,2)
);

-- Insert 10 orders (each linked to a user id 1..10)
INSERT INTO orders (id, user_id, order_date, status, total_amount, shipping_address, created_at, updated_at) VALUES (1, 1, '2025-06-01 10:00:00', 'PENDING', 59.47, '10 Rue de Paris, 75001 Paris, FR', '2025-06-01 10:00:00', NULL);
INSERT INTO orders (id, user_id, order_date, status, total_amount, shipping_address, created_at, updated_at) VALUES (2, 2, '2025-06-02 11:15:00', 'CONFIRMED', 29.50, '22 Avenue Victor Hugo, 75016 Paris, FR', '2025-06-02 11:15:00', NULL);
INSERT INTO orders (id, user_id, order_date, status, total_amount, shipping_address, created_at, updated_at) VALUES (3, 3, '2025-06-03 12:30:00', 'SHIPPED', 39.90, '5 Place Gambetta, 69003 Lyon, FR', '2025-06-03 12:30:00', NULL);
INSERT INTO orders (id, user_id, order_date, status, total_amount, shipping_address, created_at, updated_at) VALUES (4, 3, '2025-06-04 13:45:00', 'DELIVERED', 45.25, '14 Boulevard Saint-Germain, 75005 Paris, FR', '2025-06-04 13:45:00', NULL);
INSERT INTO orders (id, user_id, order_date, status, total_amount, shipping_address, created_at, updated_at) VALUES (5, 5, '2025-06-05 15:00:00', 'CANCELLED', 17.50, '3 Rue Nationale, 59000 Lille, FR', '2025-06-05 15:00:00', NULL);
INSERT INTO orders (id, user_id, order_date, status, total_amount, shipping_address, created_at, updated_at) VALUES (6, 6, '2025-06-06 09:20:00', 'CONFIRMED', 49.99, '8 Rue du Commerce, 44000 Nantes, FR', '2025-06-06 09:20:00', NULL);
INSERT INTO orders (id, user_id, order_date, status, total_amount, shipping_address, created_at, updated_at) VALUES (7, 7, '2025-06-07 10:40:00', 'PENDING', 20.80, '12 Rue de la République, 13002 Marseille, FR', '2025-06-07 10:40:00', NULL);
INSERT INTO orders (id, user_id, order_date, status, total_amount, shipping_address, created_at, updated_at) VALUES (8, 8, '2025-06-08 11:50:00', 'SHIPPED', 45.00, '1 Place de la Comédie, 34000 Montpellier, FR', '2025-06-08 11:50:00', NULL);
INSERT INTO orders (id, user_id, order_date, status, total_amount, shipping_address, created_at, updated_at) VALUES (9, 9, '2025-06-09 13:05:00', 'DELIVERED', 17.98, '7 Rue du Château, 21000 Dijon, FR', '2025-06-09 13:05:00', NULL);
INSERT INTO orders (id, user_id, order_date, status, total_amount, shipping_address, created_at, updated_at) VALUES (10, 10, '2025-06-10 15:30:00', 'CONFIRMED', 74.15, '25 Rue du Moulin, 67000 Strasbourg, FR', '2025-06-10 15:30:00', NULL);

-- Insert 10 order_items matching products 1..10
INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES (1, 1, 1, 'Wireless Mouse', 2, 19.99, 39.98);
INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES (2, 1, 5, 'Notebook A5', 3, 3.50, 10.50);
INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES (3, 1, 9, 'Chocolate Box', 1, 8.99, 8.99);
INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES (4, 2, 2, 'USB-C Charger', 1, 29.50, 29.50);
INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES (5, 3, 3, 'Java Programming', 1, 39.90, 39.90);
INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES (6, 4, 4, 'Organic Honey', 3, 12.75, 38.25);
INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES (7, 4, 5, 'Notebook A5', 2, 3.50, 7.00);
INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES (8, 5, 5, 'Notebook A5', 5, 3.50, 17.50);
INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES (9, 6, 6, 'Bluetooth Speaker', 1, 49.99, 49.99);
INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES (10, 7, 7, 'Cooking Oil 1L', 4, 5.20, 20.80);
INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES (11, 8, 8, 'Data Structures', 1, 45.00, 45.00);
INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES (12, 9, 9, 'Chocolate Box', 2, 8.99, 17.98);
INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES (13, 10, 10, 'Pen Set', 3, 6.75, 20.25);
INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES (14, 10, 2, 'USB-C Charger', 1, 29.50, 29.50);
INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES (15, 10, 5, 'Notebook A5', 4, 3.50, 14.00);
INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES (16, 10, 7, 'Cooking Oil 1L', 2, 5.20, 10.40);

