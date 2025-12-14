package com.product.products.infrastructure.exception;

public class ExternalUsageException extends RuntimeException {
    public ExternalUsageException(String message) {
        super(message);
    }

    public ExternalUsageException(String serviceName, String responseCode, Exception ex) {
        super("La ressource demandée est utilisée dans un service externe: "+ ex.getMessage());
    }
}
