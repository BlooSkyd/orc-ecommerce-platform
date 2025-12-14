package com.product.products.infrastructure.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryValidatorTest {

    private final CategoryValidator validator = new CategoryValidator();

    @Test
    void isValid_returnsTrueForValidCategory() {
        assertThat(validator.isValid("ELECTRONICS", null)).isTrue();
    }

    @Test
    void isValid_returnsFalseForInvalidCategory() {
        assertThat(validator.isValid("INVALID", null)).isFalse();
    }

    @Test
    void isValid_returnsTrueForNull() {
        assertThat(validator.isValid(null, null)).isTrue();
    }
}
