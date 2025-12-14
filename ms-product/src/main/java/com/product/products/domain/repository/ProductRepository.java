package com.product.products.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.product.products.domain.entity.Product;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Product.
 * Best practices :
 * - Utilisation de Spring Data JPA pour réduire le code boilerplate
 * - Méthodes de requête dérivées pour une meilleure lisibilité
 * - Queries personnalisées avec @Query si nécessaire
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Recherche un produit par id (méthode de requête dérivée)
     */
    Optional<Product> findById(Long id);

    /**
     * Recherche des produits par nom (insensible à la casse)
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> searchByName(String name);


    /**
     * Recherche les produits par categorie
     */
    List<Product> searchByCategory(String category);


    /**
     * Compte le nombre d'utilisateurs actifs
     */
    @Query("SELECT p FROM Product p WHERE p.stock > 0")
    List<Product> findAvailableProduct();

    /**
     * Compte le nombre d'utilisateurs actifs
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.active = true")
    long countActiveProducts();
}
