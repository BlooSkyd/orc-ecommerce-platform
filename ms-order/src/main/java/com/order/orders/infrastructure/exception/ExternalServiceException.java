package com.order.orders.infrastructure.exception;

public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String serviceName, String responseCode, Exception ex) {
        super(String.format("Une erreur est survenue sur le service externe %s avec le code retour %s : %s", serviceName, responseCode, ex.getMessage()));
    }
}
