package com.order.orders.configuration;

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
            Long maxOrderId = jdbcTemplate.queryForObject("SELECT COALESCE(MAX(id),0) FROM orders", Long.class);
            long nextOrder = (maxOrderId == null ? 1L : maxOrderId + 1L);
            jdbcTemplate.execute("ALTER TABLE orders ALTER COLUMN id RESTART WITH " + nextOrder);

            Long maxItemId = jdbcTemplate.queryForObject("SELECT COALESCE(MAX(id),0) FROM order_items", Long.class);
            long nextItem = (maxItemId == null ? 1L : maxItemId + 1L);
            jdbcTemplate.execute("ALTER TABLE order_items ALTER COLUMN id RESTART WITH " + nextItem);

            log.info("IdentityInitializer set orders.id next={} and order_items.id next={}", nextOrder, nextItem);
        } catch (Exception e) {
            log.warn("Failed to initialize identity sequences: {}", e.getMessage());
        }
    }
}
