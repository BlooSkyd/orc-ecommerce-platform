package com.product.products.infrastructure.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.product.products.domain.entity.Category;

public class CategoryValidator implements ConstraintValidator<ValidCategory, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            Category.valueOf(value);
            return true; // Conversion réussie, la valeur est valide
        } catch (IllegalArgumentException e) {
            return false; // Conversion échouée, la valeur n'existe pas dans l'Enum
        }
    }
}