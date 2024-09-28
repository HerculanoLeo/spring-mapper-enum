package com.herculanoleo.spring.me.models.exception;

public sealed class InvalidEnumException extends RuntimeException permits InvalidGenericEnumException, InvalidValueEnumException {

    public InvalidEnumException() {
        super();
    }

    public InvalidEnumException(final String message) {
        super(message);
    }

}
