package com.product.products.infrastructure.exception;

public class FieldValueException extends RuntimeException {
    public FieldValueException(String message) {
        super(message);
    }

    public FieldValueException(String resourceName, String fieldName, Object fieldValue, String rule) {
        super(String.format("Une erreur est survenue sur l'objet %s et son champ %s à la valeur '%s' : %s.", resourceName, fieldName, fieldValue, rule));
    }
}
