package com.product.products.application.service;

import com.product.products.application.dto.ProductResponseDTO;
import com.product.products.application.mapper.ProductMapper;
import com.product.products.domain.entity.Category;
import com.product.products.domain.entity.Product;
import com.product.products.domain.repository.ProductRepository;
import com.product.products.infrastructure.exception.FieldValueException;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private MeterRegistry meterRegistry;

    @InjectMocks
    private ProductService productService;

    @Test
    void updateStock_shouldThrowWhenStockNegative() {
        Product product = Product.builder()
                .id(1L)
                .name("P")
                .description("descdescdesc")
                .price(new BigDecimal("5.00"))
                .stock(2)
                .category(Category.OTHER)
                .active(true)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.updateStock(1L, -3))
                .isInstanceOf(FieldValueException.class)
                .hasMessageContaining("le nouveau stock doit être positif");

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, never()).save(any());
    }

}
