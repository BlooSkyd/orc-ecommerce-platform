package com.order.orders.infrastructure.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = OrderStatusValidator.class) // Lien vers la classe de validation
@Target({ElementType.FIELD, ElementType.PARAMETER}) // Où l'appliquer (champs et paramètres)
@Retention(RetentionPolicy.RUNTIME) // Doit être disponible à l'exécution
public @interface ValidStatus {

    String message() default "Le statut n'est pas valide. Les statuts permises sont: PENDING, CONFIRMED, SHIPPED, DELIVERED et CANCELLED (avec 2 L ^^ ).";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}