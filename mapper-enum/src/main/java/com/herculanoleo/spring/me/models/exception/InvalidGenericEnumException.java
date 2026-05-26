package com.herculanoleo.spring.me.models.exception;

/**
 * Thrown when {@link com.herculanoleo.spring.me.models.enums.MapperEnum#generic(Class)} is called
 * with a type that is not a Java enum, or when a custom {@link com.herculanoleo.spring.me.models.enums.MapperEnum#getGeneric()}
 * implementation returns a non-enum instance.
 */
public final class InvalidGenericEnumException extends InvalidEnumException {

    public InvalidGenericEnumException() {
        super("generic value is not a enum but a common class");
    }

    public InvalidGenericEnumException(final String message) {
        super(message);
    }

}
