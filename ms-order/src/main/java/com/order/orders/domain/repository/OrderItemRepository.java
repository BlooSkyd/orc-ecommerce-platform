package com.order.orders.domain.repository;

import com.order.orders.domain.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour l'entité Order.
 * Best practices :
 * - Utilisation de Spring Data JPA pour réduire le code boilerplate
 * - Méthodes de requête dérivées pour une meilleure lisibilité
 * - Queries personnalisées avec @Query si nécessaire
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * Recherche des produits par nom (insensible à la casse)
     */
    List<OrderItem> searchByOrderId(Long orderId);

    /**
     * Vérifie si au moins un OrderItem fait référence au produit donné
     */
    boolean existsByProductId(Long productId);


}
