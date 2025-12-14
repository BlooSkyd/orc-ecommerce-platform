package com.order.orders.application.service;

import com.order.orders.application.dto.*;
import com.order.orders.application.mapper.OrderItemMapper;
import com.order.orders.application.mapper.OrderMapper;
import com.order.orders.domain.entity.Order;
import com.order.orders.domain.entity.OrderItem;
import com.order.orders.domain.entity.OrderStatus;
import com.order.orders.domain.repository.OrderItemRepository;
import com.order.orders.domain.repository.OrderRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
	@org.mockito.Mock
	private OrderItemRepository orderItemRepository;

	@org.mockito.Mock
	private OrderRepository orderRepository;

	private final OrderMapper orderMapper = new OrderMapper();

	@org.mockito.Mock
	private OrderItemMapper orderItemMapper;

	@org.mockito.Mock
	private MeterRegistry meterRegistry;

	@org.mockito.Mock
	private WebClient productServiceWebClient;

	@org.mockito.Mock
	private WebClient userServiceWebClient;

	private OrderService orderService;

	@org.junit.jupiter.api.BeforeEach
	void setUp() {
		orderService = new OrderService(orderItemRepository, orderRepository, orderMapper,
				orderItemMapper, meterRegistry, productServiceWebClient, userServiceWebClient);
	}

	@Test
	@DisplayName("getAllOrders returns mapped DTOs")
	void getAllOrders_returnsMappedDtos() {
		var item = OrderItem.builder()
				.id(10L)
				.orderId(1L)
				.productId(5L)
				.productName("Prod")
				.quantity(2)
				.unitPrice(new java.math.BigDecimal("5.00"))
				.subtotal(new java.math.BigDecimal("10.00"))
				.build();

		var order = Order.builder()
				.id(1L)
				.userId(42L)
				.status(OrderStatus.PENDING)
				.totalAmount(new java.math.BigDecimal("10.00"))
				.shippingAddress("123 Main Street, City")
				.build();

		order.setItems(new java.util.ArrayList<>());
		order.getItems().add(item);

		when(orderRepository.findAll()).thenReturn(java.util.List.of(order));

		List<OrderResponseDTO> results = orderService.getAllOrders();

		assertThat(results).hasSize(1);
		OrderResponseDTO dto = results.get(0);
		assertThat(dto.getId()).isEqualTo(1L);
		assertThat(dto.getUserId()).isEqualTo(42L);
		assertThat(dto.getShippingAddress()).isEqualTo("123 Main Street, City");
	}
}