package com.order.orders.application.mapper;

import com.order.orders.application.dto.OrderDetailsResponseDTO;
import com.order.orders.application.dto.OrderItemResponseDTO;
import com.order.orders.application.dto.OrderRequestDTO;
import com.order.orders.application.dto.OrderResponseDTO;
import com.order.orders.domain.entity.Order;
import com.order.orders.domain.entity.OrderItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderMapperTest {

    private final OrderMapper mapper = new OrderMapper();

    @Test
    void toEntity_mapsRequestFields() {
        OrderRequestDTO req = OrderRequestDTO.builder()
                .userId(7L)
                .shippingAddress("10 Rue de Test, Paris")
                .build();

        Order entity = mapper.toEntity(req);

        assertThat(entity.getUserId()).isEqualTo(7L);
        assertThat(entity.getShippingAddress()).isEqualTo("10 Rue de Test, Paris");
    }

    @Test
    void toDto_and_toDetailsDto_mapFieldsCorrectly() {
        OrderItem item = OrderItem.builder()
                .id(3L)
                .orderId(1L)
                .productId(11L)
                .productName("Foo")
                .quantity(1)
                .unitPrice(new BigDecimal("2.50"))
                .subtotal(new BigDecimal("2.50"))
                .build();

        Order order = Order.builder()
                .id(1L)
                .userId(9L)
                .orderDate(LocalDateTime.now())
                .status(null)
                .totalAmount(new BigDecimal("2.50"))
                .shippingAddress("Addr")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        order.setItems(new java.util.ArrayList<>());
        order.getItems().add(item);

        OrderResponseDTO dto = mapper.toDto(order);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getUserId()).isEqualTo(9L);
        assertThat(dto.getTotalAmount()).isEqualTo(new BigDecimal("2.50"));
        assertThat(dto.getItemAmount()).isEqualTo(1);

        OrderItemResponseDTO itemDto = OrderItemResponseDTO.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .build();

        OrderDetailsResponseDTO details = mapper.toDetailsDto(dto, List.of(itemDto));
        assertThat(details.getOrderItems()).hasSize(1);
        assertThat(details.getUserId()).isEqualTo(dto.getUserId());
    }
}
