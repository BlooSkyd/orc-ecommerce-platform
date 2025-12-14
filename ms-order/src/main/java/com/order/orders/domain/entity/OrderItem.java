package com.order.orders.domain.entity;

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

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @NotNull
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @NotBlank(message = "Le nom ne peut pas être vide")
    @Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @NotNull(message = "La quantité ne peut pas être nulle")
    @Positive(message = "La quantité doit être supérieure à 0")
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Positive(message = "Le prix unitaire total doit être positif")
    @Digits(integer=10, fraction=2, message = "Le prix unitaire doit avoir au plus 10 chiffres avant la virgule, et maximum 2 après la virgule")
    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Positive(message = "Le sous total doit être positif")
    @Digits(integer=10, fraction=2, message = "Le sous total doit avoir au plus 10 chiffres avant la virgule, et maximum 2 après la virgule")
    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal;

}
