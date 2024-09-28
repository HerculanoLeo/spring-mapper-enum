package com.herculanoleo.spring.me.models.exception;

public final class InvalidGenericEnumException extends InvalidEnumException {

    public InvalidGenericEnumException() {
        super("generic value is not a enum but a common class");
    }

    public InvalidGenericEnumException(final String message) {
        super(message);
    }

}
