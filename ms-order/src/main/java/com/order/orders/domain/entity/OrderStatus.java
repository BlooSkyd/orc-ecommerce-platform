package com.order.orders.domain.entity;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    public int getScore() {
        return switch (this) {
            case PENDING -> 1;
            case CONFIRMED -> 2;
            case SHIPPED -> 3;
            case DELIVERED, CANCELLED -> 4;
        };
    }
}
