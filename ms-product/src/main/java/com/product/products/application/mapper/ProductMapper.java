package com.product.products.application.mapper;

import com.product.products.domain.entity.Category;
import org.springframework.stereotype.Component;

import com.product.products.application.dto.ProductRequestDTO;
import com.product.products.application.dto.ProductResponseDTO;
import com.product.products.domain.entity.Product;

import java.util.Locale;

/**
 * Mapper pour convertir entre User et ses DTOs.
 * Best practices :
 * - Séparation de la logique de mapping
 * - Conversion centralisée
 * - Facilite les tests unitaires
 */
@Component
public class ProductMapper {

    /**
     * Convertit un UserRequestDTO en entité User
     */
    public Product toEntity(ProductRequestDTO dto) {
        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .category(Category.valueOf(dto.getCategory()))
                .price(dto.getPrice())
                .imageUrl(dto.getImageUrl())
                .stock(dto.getStock())
                .active(dto.getActive())
                .build();
    }

    /**
     * Convertit une entité User en UserResponseDTO
     */
    public ProductResponseDTO toDto(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .category(String.valueOf(product.getCategory()))
                .price(product.getPrice())
                .stock(product.getStock())
                .active(product.getActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    /**
     * Met à jour une entité User existante avec les données du DTO
     */
    public void updateEntityFromDto(ProductRequestDTO dto, Product product) {
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategory(Category.valueOf(dto.getCategory()));
        product.setStock(dto.getStock());
        product.setImageUrl(dto.getImageUrl());
        product.setActive(dto.getActive());
    }
}
