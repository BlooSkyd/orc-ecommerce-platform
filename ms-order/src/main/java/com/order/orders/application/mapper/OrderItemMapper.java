package com.order.orders.application.mapper;

import com.order.orders.application.dto.OrderItemRequestDTO;
import com.order.orders.application.dto.OrderItemResponseDTO;
import com.order.orders.domain.entity.OrderItem;
import com.product.products.application.dto.ProductRequestDTO;
import com.product.products.application.dto.ProductResponseDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderItemMapper {

    /**
     * Convertit un UserRequestDTO en entité User
     */
    public OrderItem toEntity(OrderItemRequestDTO itemDTO, ProductResponseDTO productDTO, Long orderId) {
        return OrderItem.builder()
                .orderId(orderId)
                .productId(itemDTO.getProductId())
                .productName(productDTO.getName())
                .quantity(itemDTO.getQuantity())
                .unitPrice(productDTO.getPrice())
                .subtotal(productDTO.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())))
                .build();
    }

    /**
     * Convertit une entité User en UserResponseDTO
     */
    public OrderItemResponseDTO toDto(OrderItem orderItem) {
        return OrderItemResponseDTO.builder()
                .id(orderItem.getId())
                .orderId(orderItem.getOrderId())
                .productId(orderItem.getProductId())
                .productName(orderItem.getProductName())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .subtotal(orderItem.getSubtotal())
                .build();
    }
}
