-- Données initiales pour la base de données H2
-- Ce script est exécuté automatiquement au démarrage de l'application
-- Création de la table users
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- Insert 10 users
INSERT INTO users (id, first_name, last_name, email, active, created_at, updated_at) VALUES (1, 'Alice', 'Durand', 'alice.durand@example.com', TRUE, '2025-01-01 09:00:00', NULL);
INSERT INTO users (id, first_name, last_name, email, active, created_at, updated_at) VALUES (2, 'Bob', 'Martin', 'bob.martin@example.com', TRUE, '2025-01-02 10:15:00', NULL);
INSERT INTO users (id, first_name, last_name, email, active, created_at, updated_at) VALUES (3, 'Claire', 'Petit', 'claire.petit@example.com', TRUE, '2025-01-03 11:30:00', NULL);
INSERT INTO users (id, first_name, last_name, email, active, created_at, updated_at) VALUES (4, 'David', 'Leroy', 'david.leroy@example.com', FALSE, '2025-01-04 12:45:00', NULL);
INSERT INTO users (id, first_name, last_name, email, active, created_at, updated_at) VALUES (5, 'Emilie', 'Bernard', 'emilie.bernard@example.com', TRUE, '2025-01-05 14:00:00', NULL);
INSERT INTO users (id, first_name, last_name, email, active, created_at, updated_at) VALUES (6, 'Francois', 'Moreau', 'francois.moreau@example.com', FALSE, '2025-01-06 09:20:00', NULL);
INSERT INTO users (id, first_name, last_name, email, active, created_at, updated_at) VALUES (7, 'Geraldine', 'Richard', 'geraldine.richard@example.com', TRUE, '2025-01-07 10:40:00', NULL);
INSERT INTO users (id, first_name, last_name, email, active, created_at, updated_at) VALUES (8, 'Hugo', 'Thomas', 'hugo.thomas@example.com', TRUE, '2025-01-08 11:50:00', NULL);
INSERT INTO users (id, first_name, last_name, email, active, created_at, updated_at) VALUES (9, 'Isabelle', 'Dubois', 'isabelle.dubois@example.com', TRUE, '2025-01-09 13:05:00', NULL);
INSERT INTO users (id, first_name, last_name, email, active, created_at, updated_at) VALUES (10, 'Julien', 'Fabre', 'julien.fabre@example.com', FALSE, '2025-01-10 15:30:00', NULL);

