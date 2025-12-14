package com.product.products.application.mapper;

import com.product.products.application.dto.ProductRequestDTO;
import com.product.products.domain.entity.Category;
import com.product.products.domain.entity.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductMapperTest {

    private final ProductMapper mapper = new ProductMapper();

    @Test
    void toEntity_mapsAllFields() {
        ProductRequestDTO dto = ProductRequestDTO.builder()
                .name("Phone")
                .description("A smartphone with many features")
                .price(new BigDecimal("199.99"))
                .stock(10)
                .category("ELECTRONICS")
                .imageUrl("http://img")
                .active(true)
                .build();

        Product product = mapper.toEntity(dto);

        assertThat(product.getName()).isEqualTo("Phone");
        assertThat(product.getDescription()).isEqualTo("A smartphone with many features");
        assertThat(product.getPrice()).isEqualByComparingTo(new BigDecimal("199.99"));
        assertThat(product.getStock()).isEqualTo(10);
        assertThat(product.getCategory()).isEqualTo(Category.ELECTRONICS);
        assertThat(product.getImageUrl()).isEqualTo("http://img");
        assertThat(product.getActive()).isTrue();
    }

    @Test
    void updateEntityFromDto_updatesFields() {
        ProductRequestDTO dto = ProductRequestDTO.builder()
                .name("NewName")
                .description("New description longer")
                .price(new BigDecimal("9.99"))
                .stock(5)
                .category("BOOKS")
                .imageUrl("img2")
                .active(false)
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Old")
                .description("Old desc")
                .price(new BigDecimal("1.00"))
                .stock(1)
                .category(Category.OTHER)
                .imageUrl("old")
                .active(true)
                .build();

        mapper.updateEntityFromDto(dto, product);

        assertThat(product.getName()).isEqualTo("NewName");
        assertThat(product.getCategory()).isEqualTo(Category.BOOKS);
        assertThat(product.getActive()).isFalse();
    }
}
