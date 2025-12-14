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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED')", name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "total_amount")
    @Positive(message = "Le sous total doit être positif")
    @Digits(integer = 10, fraction = 2, message = "Le sous total doit avoir au plus 10 chiffres avant la virgule, et maximum 2 après la virgule")
    private BigDecimal totalAmount = java.math.BigDecimal.ZERO;

    @Column(name = "shipping_address")
    private String shippingAddress;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Transient
    private ArrayList<OrderItem> items = new ArrayList<>();

    public void addItem(OrderItem item) {
        if (item == null) return;
        if (this.totalAmount == null) this.totalAmount = java.math.BigDecimal.ZERO;
        this.totalAmount = this.totalAmount.add(item.getSubtotal() != null ? item.getSubtotal() : java.math.BigDecimal.ZERO);
        if (items == null) items = new ArrayList<>();
        this.items.add(item);
    }

    public Order(Order clone) {
        this.id = clone.id;
        this.userId = clone.userId;
        this.orderDate = clone.orderDate;
        this.status = clone.status;
        this.totalAmount = clone.totalAmount;
        this.shippingAddress = clone.shippingAddress;
        this.createdAt = clone.createdAt;
        this.updatedAt = clone.updatedAt;
        this.items = clone.items;
    }
}
