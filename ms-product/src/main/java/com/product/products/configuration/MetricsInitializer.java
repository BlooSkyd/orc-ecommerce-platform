package com.product.products.configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.product.products.domain.entity.Category;
import com.product.products.domain.entity.Product;
import com.product.products.domain.repository.ProductRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MetricsInitializer {

    private final ProductRepository productRepository;
    private final MeterRegistry meterRegistry;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional(readOnly = true)
    public void initMetricsOnStartup() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) return;

        Map<Category, Long> counts = products.stream()
                .filter(p -> p.getCategory() != null)
                .collect(Collectors.groupingBy(Product::getCategory, Collectors.counting()));

        counts.forEach((cat, cnt) -> {
            Counter.builder("products.created." + cat.toString().toLowerCase())
                    .description("Nombre de produits créés dans la catégorie "+ cat.toString().toLowerCase())
                    .tag("type", "product")
                    .register(meterRegistry)
                    .increment(cnt.doubleValue());
        });
    }
}
