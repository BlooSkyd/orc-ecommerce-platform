package com.product.products.infrastructure.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CategoryValidator.class) // Lien vers la classe de validation
@Target({ElementType.FIELD, ElementType.PARAMETER}) // Où l'appliquer (champs et paramètres)
@Retention(RetentionPolicy.RUNTIME) // Doit être disponible à l'exécution
public @interface ValidCategory {

    String message() default "La catégorie n'est pas valide. Les catégories permises sont: ELECTRONICS, BOOKS, FOOD, OTHER";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}