package com.product.products.infrastructure.metrics;

import com.product.products.domain.repository.ProductRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LowStockMetrics {

    private final ProductRepository productRepository;
    private final MeterRegistry meterRegistry;

    private static final int LOW_STOCK_THRESHOLD = 5;

    @PostConstruct
    public void init() {
        Gauge.builder("products.low_stock.count", this, LowStockMetrics::getLowStockCount)
                .description("Number of products with stock lower than " + LOW_STOCK_THRESHOLD)
                .register(meterRegistry);
    }

    public double getLowStockCount() {
        try {
            return productRepository.findAll()
                    .stream()
                    .filter(p -> p.getStock() < LOW_STOCK_THRESHOLD)
                    .count();
        } catch (Exception e) {
            // In case of error, return 0 so metric remains available
            return 0d;
        }
    }
}
