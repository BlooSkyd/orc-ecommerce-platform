package com.order.orders.infrastructure.health;

import com.order.orders.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Health Indicator personnalisé pour vérifier l'état de la base de données.
 * Best practices :
 * - Implémente HealthIndicator pour les checks personnalisés
 * - Fournit des détails utiles pour le debugging
 * - Gère les exceptions proprement
 * - Utilisé par /actuator/health
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseHealthIndicator implements HealthIndicator {

    private final OrderRepository productRepository;


    private final WebClient productServiceWebClient;
    private final WebClient userServiceWebClient;

    private final String healthEndpoint = "actuator/health";


    @Override
    public Health health() {

        // Vérifie la connexion aux services
        boolean isUserServiceUp = Boolean.TRUE.equals(
                    userServiceWebClient.get()
                            .uri(healthEndpoint)
                            .retrieve()
                            .toBodilessEntity()
                            .map(response -> response.getStatusCode().is2xxSuccessful())
                            .onErrorReturn(false)
                            .block()
                    );

        boolean isProductServiceUp = Boolean.TRUE.equals(
                productServiceWebClient.get()
                        .uri(healthEndpoint)
                        .retrieve()
                        .toBodilessEntity()
                        .map(response -> response.getStatusCode().is2xxSuccessful())
                        .onErrorReturn(false)
                        .block()
        );


        if (isUserServiceUp && isProductServiceUp) {
            return Health.up().build();
        }

        return Health.down()
                .withDetail("UserService", isUserServiceUp ? "UP" : "DOWN")
                .withDetail("ProductService", isProductServiceUp ? "UP" : "DOWN")
                .build();

    }

}
