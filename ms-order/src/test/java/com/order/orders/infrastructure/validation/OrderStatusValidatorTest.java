package com.order.orders.infrastructure.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusValidatorTest {

    private final OrderStatusValidator validator = new OrderStatusValidator();

    @Test
    void isValid_returnsTrueForValidEnumName() {
        assertThat(validator.isValid("PENDING", null)).isTrue();
        assertThat(validator.isValid("DELIVERED", null)).isTrue();
    }

    @Test
    void isValid_returnsTrueForNull() {
        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    void isValid_returnsFalseForInvalidName() {
        assertThat(validator.isValid("UNKNOWN_STATUS", null)).isFalse();
    }
}
