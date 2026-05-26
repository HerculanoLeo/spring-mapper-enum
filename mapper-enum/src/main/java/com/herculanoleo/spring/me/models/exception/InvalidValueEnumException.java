package com.herculanoleo.spring.me.models.exception;

/**
 * Thrown when {@link com.herculanoleo.spring.me.models.enums.MapperEnum#fromValue(String, Class)}
 * cannot resolve a string and the enum type has no {@link com.herculanoleo.spring.me.models.enums.MapperEnum#getGeneric()} fallback.
 */
public final class InvalidValueEnumException extends InvalidEnumException {

    /**
     * @param message detail message, often including the invalid value and optional
     *                {@link com.herculanoleo.spring.me.models.enums.MapperEnum#getErrorMessage()}
     */
    public InvalidValueEnumException(final String message) {
        super(message);
    }

}
