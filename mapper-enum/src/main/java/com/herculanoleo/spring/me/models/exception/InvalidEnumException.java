package com.herculanoleo.spring.me.models.exception;

/**
 * Base runtime exception for {@link com.herculanoleo.spring.me.models.enums.MapperEnum} conversion failures.
 *
 * @see InvalidValueEnumException
 * @see InvalidGenericEnumException
 */
public sealed class InvalidEnumException extends RuntimeException permits InvalidGenericEnumException, InvalidValueEnumException {

    public InvalidEnumException() {
        super();
    }

    public InvalidEnumException(final String message) {
        super(message);
    }

}
