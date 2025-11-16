package com.restaurantefiap.exception;

public class DuplicateResourceException extends RuntimeException{
    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String field, String value) {
        super(String.format("%s '%s' já está cadastrado no sistema.", field, value));
    }
}
