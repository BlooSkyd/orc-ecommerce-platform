package com.product.products.infrastructure.health;

import com.product.products.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Health Indicator personnalisé qui vérifie le nombre de produits dont le stock est bas (<5).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LowStockHealthIndicator implements HealthIndicator {

    private final ProductRepository productRepository;
    private static final int LOW_STOCK_THRESHOLD = 5;

    @Override
    public Health health() {
        try {
            long lowStockCount = productRepository.findAll()
                    .stream()
                    .filter(p -> p.getStock() < LOW_STOCK_THRESHOLD)
                    .count();

            log.debug("Health check low-stock - products below {}: {}", LOW_STOCK_THRESHOLD, lowStockCount);

            if (lowStockCount > 0) {
                return Health.down()
                        .withDetail("lowStockThreshold", LOW_STOCK_THRESHOLD)
                        .withDetail("lowStockCount", lowStockCount)
                        .build();
            } else {
                return Health.up()
                        .withDetail("lowStockThreshold", LOW_STOCK_THRESHOLD)
                        .withDetail("lowStockCount", lowStockCount)
                        .build();
            }

        } catch (Exception e) {
            log.error("LowStock health check failed", e);
            return Health.down(e).withDetail("error", e.getMessage()).build();
        }
    }
}
