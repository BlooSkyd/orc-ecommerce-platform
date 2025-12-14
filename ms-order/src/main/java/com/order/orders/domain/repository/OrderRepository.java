package com.order.orders.domain.repository;

import com.order.orders.domain.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.order.orders.domain.entity.Order;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Order.
 * Best practices :
 * - Utilisation de Spring Data JPA pour réduire le code boilerplate
 * - Méthodes de requête dérivées pour une meilleure lisibilité
 * - Queries personnalisées avec @Query si nécessaire
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Recherche un produit par id (méthode de requête dérivée)
     */
    Optional<Order> findById(Long id);

    /**
     * Recherche les produits par categorie
     */
    List<Order> searchByStatus(String status);


    /**
     * Recherche les commandes d'un utilisateur
     */
    @Query("SELECT o FROM Order o WHERE o.userId = :userId")
    List<Order> findAllUserOrder(Long userId);

    List<Order> findByStatus(OrderStatus status);
}
