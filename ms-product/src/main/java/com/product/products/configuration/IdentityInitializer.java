package com.product.products.configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IdentityInitializer {

    private static final Logger log = LoggerFactory.getLogger(IdentityInitializer.class);

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        try {
            Long maxOrderId = jdbcTemplate.queryForObject("SELECT COALESCE(MAX(id),0) FROM products", Long.class);
            long nextOrder = (maxOrderId == null ? 1L : maxOrderId + 1L);
            jdbcTemplate.execute("ALTER TABLE products ALTER COLUMN id RESTART WITH " + nextOrder);

            log.info("IdentityInitializer set orders.id next={}", nextOrder);
        } catch (Exception e) {
            log.warn("Failed to initialize identity sequences: {}", e.getMessage());
        }
    }
}
