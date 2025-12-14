package com.order.orders.configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.order.orders.application.service.OrderService;
import com.order.orders.domain.entity.Order;
import com.order.orders.domain.entity.OrderStatus;
import com.order.orders.domain.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MetricsInitializer {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final MeterRegistry meterRegistry;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional(readOnly = true)
    public void initMetricsOnStartup() {
        List<Order> orders = orderRepository.findAll();
        if (!orders.isEmpty()) {
            Map<OrderStatus, Long> counts = orders.stream()
                    .filter(o -> o.getStatus() != null)
                    .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));

            counts.forEach((status, cnt) -> {
                Counter.builder("orders." + status.toString().toLowerCase())
                        .register(meterRegistry)
                        .increment(cnt.doubleValue());
            });
        }

        // initialiser daily total (somme des commandes dont orderDate est aujourd'hui)
        BigDecimal todayTotal = orders.stream()
                .filter(o -> o.getOrderDate() != null && o.getOrderDate().toLocalDate().isEqual(LocalDate.now()))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        orderService.setDailyTotal(todayTotal.doubleValue());
    }
}
