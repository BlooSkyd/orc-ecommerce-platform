package com.order.orders.infrastructure.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Health Indicator personnalisé pour vérifier les services externes.
 * Best practice : Permet de monitorer la disponibilité des dépendances externes
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalServicesHealthIndicator implements HealthIndicator {

    private final WebClient productServiceWebClient;
    private final WebClient userServiceWebClient;

    private final String healthEndpoint = "actuator/health";

    @Override
    public Health health() {
        try {
            // Simulation d'un check de service externe
            // Dans un cas réel, vous feriez un appel HTTP, une connexion à un service, etc.
            boolean isUserServiceUp = checkUserService();
            boolean isProductServiceUp = checkProductService();
            
            if (isUserServiceUp && isProductServiceUp) {
                return Health.up()
                        .withDetail("userService", "UP")
                        .withDetail("productService", "UP")
                        .build();
            } else {
                return Health.down()
                        .withDetail("userService", isUserServiceUp ? "UP" : "DOWN")
                        .withDetail("productService", isProductServiceUp ? "UP" : "DOWN")
                        .build();
            }
            
        } catch (Exception e) {
            log.error("Health check external services failed", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }

    private boolean checkUserService() {
        return Boolean.TRUE.equals(
                userServiceWebClient.get()
                        .uri(healthEndpoint)
                        .retrieve()
                        .toBodilessEntity()
                        .map(response -> response.getStatusCode().is2xxSuccessful())
                        .onErrorReturn(false)
                        .block()
        );
    }

    private boolean checkProductService() {
        return Boolean.TRUE.equals(
                productServiceWebClient.get()
                        .uri(healthEndpoint)
                        .retrieve()
                        .toBodilessEntity()
                        .map(response -> response.getStatusCode().is2xxSuccessful())
                        .onErrorReturn(false)
                        .block()
        );
    }
}
