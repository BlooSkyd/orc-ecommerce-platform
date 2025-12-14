package com.product.products.application.dto;

import java.math.BigDecimal;

import com.product.products.infrastructure.validation.ValidCategory;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la création d'un utilisateur.
 * Best practices :
 * - Séparation des DTOs Request/Response
 * - Validation au niveau DTO
 * - Utilisation de Builder pattern
 * - Pas d'exposition de l'entité directement dans l'API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {

    @NotBlank(message = "Le nom ne peut pas être vide")
    @Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
    private String name;

    @NotBlank(message = "La description ne peut pas être vide")
    @Size(min = 10, max = 500, message = "La description doit contenir entre 10 et 500 caractères")
    private String description;

    @Positive(message = "Le prix doit être positif")
    @Digits(integer=10, fraction=2, message = "Le prix doit avoir au plus 10 chiffres avant la virgule, et maximum 2 après la virgule")
    private BigDecimal price;

    @PositiveOrZero(message = "Le stock doit être positive ou nul")
    private int stock;

    @NotBlank(message = "La catégorie ne peut pas être vide")
    @ValidCategory
    private String category;

    private String imageUrl;

    private Boolean active = true;
}
