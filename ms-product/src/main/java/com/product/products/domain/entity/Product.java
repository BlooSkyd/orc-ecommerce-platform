package com.product.products.domain.entity;

import com.product.products.infrastructure.validation.ValidCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entité User représentant un utilisateur dans le système.
 * Best practices :
 * - Utilisation de Lombok pour réduire le boilerplate
 * - Validation avec Bean Validation
 * - Audit automatique avec @CreationTimestamp et @UpdateTimestamp
 * - Builder pattern pour une construction flexible
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom ne peut pas être vide")
    @Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "La description ne peut pas être vide")
    @Size(min = 10, max = 500, message = "La description doit contenir entre 10 et 500 caractères")
    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Positive(message = "Le prix doit être positif")
    @Digits(integer=10, fraction=2, message = "Le prix doit avoir au plus 10 chiffres avant la virgule, et maximum 2 après la virgule")
    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @NotNull(message = "Le stock ne peut pas être nul")
    @PositiveOrZero(message = "Le stock doit être positive ou nul")
    @Column(name = "stock", nullable = false)
    private int stock;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('ELECTRONICS', 'BOOKS', 'FOOD', 'OTHER')", name = "category", nullable = false)
    private Category category;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "active")
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
