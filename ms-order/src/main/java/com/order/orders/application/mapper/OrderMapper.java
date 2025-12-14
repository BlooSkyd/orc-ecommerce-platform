package com.order.orders.application.mapper;

import com.order.orders.application.dto.OrderDetailsResponseDTO;
import com.order.orders.application.dto.OrderItemResponseDTO;
import com.order.orders.application.dto.OrderRequestDTO;
import com.order.orders.domain.entity.Order;
import com.order.orders.domain.entity.OrderItem;
import com.order.orders.domain.entity.OrderStatus;
import org.springframework.stereotype.Component;

import com.order.orders.application.dto.OrderResponseDTO;

import java.util.List;

/**
 * Mapper pour convertir entre User et ses DTOs.
 * Best practices :
 * - Séparation de la logique de mapping
 * - Conversion centralisée
 * - Facilite les tests unitaires
 */
@Component
public class OrderMapper {

    public Order toEntity(OrderRequestDTO dto) {

        return Order.builder()
                .userId(dto.getUserId())
                .shippingAddress(dto.getShippingAddress())
                .status(OrderStatus.PENDING)
                .build();
    }

    /**
     * Convertit une entité User en UserResponseDTO
     */
    public OrderResponseDTO toDto(Order order) {
        return OrderResponseDTO.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .orderDate(order.getOrderDate())
                .orderStatus(String.valueOf(order.getStatus()))
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    public OrderDetailsResponseDTO toDetailsDto(OrderResponseDTO order, List<OrderItemResponseDTO> items) {
        return OrderDetailsResponseDTO.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .orderItems(items)
                .build();
    }

}
