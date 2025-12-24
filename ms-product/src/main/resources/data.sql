-- Données initiales pour la base de données H2
-- Ce script est exécuté automatiquement au démarrage de l'application
-- Création de la table products
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    stock INTEGER NOT NULL,
    category ENUM('ELECTRONICS', 'BOOKS', 'FOOD', 'OTHER') NOT NULL,
    image_url VARCHAR(500),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- Insert 10 products
INSERT INTO products (id, name, description, price, stock, category, image_url, active, created_at, updated_at) VALUES (1, 'Wireless Mouse', 'Ergonomic wireless mouse', 19.99, 120, 'ELECTRONICS', 'http://example.com/img1.jpg', TRUE, '2025-01-01 08:00:00', NULL);
INSERT INTO products (id, name, description, price, stock, category, image_url, active, created_at, updated_at) VALUES (2, 'USB-C Charger', 'Fast charging USB-C adapter', 29.50, 80, 'ELECTRONICS', 'http://example.com/img2.jpg', TRUE, '2025-01-02 08:30:00', NULL);
INSERT INTO products (id, name, description, price, stock, category, image_url, active, created_at, updated_at) VALUES (3, 'Java Programming 101', 'Comprehensive Java guide', 39.90, 45, 'BOOKS', 'http://example.com/img3.jpg', TRUE, '2025-01-03 09:00:00', NULL);
INSERT INTO products (id, name, description, price, stock, category, image_url, active, created_at, updated_at) VALUES (4, 'Organic Honey', 'Local organic honey 500g', 12.75, 200, 'FOOD', 'http://example.com/img4.jpg', TRUE, '2025-01-04 09:30:00', NULL);
INSERT INTO products (id, name, description, price, stock, category, image_url, active, created_at, updated_at) VALUES (5, 'Notebook A5', 'Ruled notebook 80 pages', 3.50, 300, 'OTHER', 'http://example.com/img5.jpg', TRUE, '2025-01-05 10:00:00', NULL);
INSERT INTO products (id, name, description, price, stock, category, image_url, active, created_at, updated_at) VALUES (6, 'Bluetooth Speaker', 'Portable speaker with bass', 49.99, 6, 'ELECTRONICS', 'http://example.com/img6.jpg', TRUE, '2025-01-06 10:30:00', NULL);
INSERT INTO products (id, name, description, price, stock, category, image_url, active, created_at, updated_at) VALUES (7, 'Cooking Oil 1L', 'Pure sunflower oil', 5.20, 150, 'FOOD', 'http://example.com/img7.jpg', TRUE, '2025-01-07 11:00:00', NULL);
INSERT INTO products (id, name, description, price, stock, category, image_url, active, created_at, updated_at) VALUES (8, 'Data Structures for Newbies', 'Algorithms and data structures book', 45.00, 10, 'BOOKS', 'http://example.com/img8.jpg', TRUE, '2025-01-08 11:30:00', NULL);
INSERT INTO products (id, name, description, price, stock, category, image_url, active, created_at, updated_at) VALUES (9, 'Chocolate Box', 'Assorted chocolates 250g', 8.99, 90, 'FOOD', 'http://example.com/img9.jpg', TRUE, '2025-01-09 12:00:00', NULL);
INSERT INTO products (id, name, description, price, stock, category, image_url, active, created_at, updated_at) VALUES (10, 'Pen Set', 'Set of 5 gel pens', 6.75, 220, 'OTHER', 'http://example.com/img10.jpg', TRUE, '2025-01-10 12:30:00', NULL);

