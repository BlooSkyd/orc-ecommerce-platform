package com.order.orders.infrastructure.validation;

import com.order.orders.domain.entity.OrderStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OrderStatusValidator implements ConstraintValidator<ValidStatus, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            OrderStatus.valueOf(value);
            return true; // Conversion réussie, la valeur est valide
        } catch (IllegalArgumentException e) {
            return false; // Conversion échouée, la valeur n'existe pas dans l'Enum
        }
    }
}