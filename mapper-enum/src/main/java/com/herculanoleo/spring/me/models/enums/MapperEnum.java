package com.herculanoleo.spring.me.models.enums;


import com.herculanoleo.spring.me.models.annotation.MapperEnumType;
import com.herculanoleo.spring.me.models.exception.InvalidGenericEnumException;
import com.herculanoleo.spring.me.models.exception.InvalidValueEnumException;

import java.util.Arrays;

/**
 * Contract for Java enums that map to a custom string value in HTTP, JSON, Feign, and the database.
 *
 * <p>Implement this interface on every enum you want the library to convert automatically.
 * Register the enum at compile time with {@link MapperEnumType} so serializers, deserializers,
 * and Spring formatters are generated and discovered via {@code META-INF/services}
 * (no runtime classpath scanning, which keeps startup predictable and native-image friendly).
 *
 * <p><strong>Example</strong>
 * <pre>{@code
 * @MapperEnumType
 * public enum TaskStatus implements MapperEnum {
 *     TODO("T"),
 *     DONE("D");
 *
 *     private final String value;
 *
 *     TaskStatus(String value) {
 *         this.value = value;
 *     }
 *
 *     @Override
 *     public String getValue() {
 *         return value;
 *     }
 * }
 * }</pre>
 *
 * @see com.herculanoleo.spring.me.models.annotation.EnableMapperEnum
 * @see MapperEnum#fromValue(String, Class)
 */
public interface MapperEnum {

    /**
     * Returns the string representation of this constant used in APIs and persistence.
     *
     * @return the mapped value (for example {@code "T"} for {@code TaskStatus.TODO})
     */
    String getValue();

    /**
     * Optional fallback constant when {@link #fromValue(String, Class)} receives an unknown or empty value.
     *
     * <p>Override on the first enum constant (convention used by {@link #generic(Class)}) to return
     * the default instance, or return {@code null} to reject unknown values with
     * {@link InvalidValueEnumException}.
     *
     * @return the generic constant, or {@code null} if none
     */
    default MapperEnum getGeneric() {
        return null;
    }

    /**
     * Optional message included in {@link InvalidValueEnumException} when conversion fails.
     *
     * @return a human-readable error message, or {@code null} to use the library default
     */
    default String getErrorMessage() {
        return null;
    }

    /**
     * Resolves the generic (fallback) constant declared by this enum type.
     *
     * @param clazz the enum class implementing {@link MapperEnum}
     * @param <E>   the enum type
     * @return the generic constant, or {@code null} if {@link #getGeneric()} is not overridden
     * @throws InvalidGenericEnumException if {@code clazz} is not an enum
     */
    static <E extends MapperEnum> E generic(Class<E> clazz) {
        if (clazz.isEnum()) {
            var generic = Arrays.stream(clazz.getEnumConstants())
                    .findFirst()
                    .map(MapperEnum::getGeneric)
                    .orElse(null);

            return clazz.cast(generic);
        }
        throw new InvalidGenericEnumException();
    }

    /**
     * Converts a string to an enum constant of the given type.
     *
     * <p>Matching order:
     * <ol>
     *   <li>{@link #getValue()} of a constant</li>
     *   <li>{@link Enum#name()} of a constant</li>
     *   <li>{@link #getGeneric()} when no constant matches</li>
     * </ol>
     *
     * @param value the incoming string (JSON body, query parameter, database column, etc.)
     * @param clazz the target enum class
     * @param <E>   the enum type
     * @return the matching constant, or the generic fallback
     * @throws InvalidValueEnumException if no constant matches and no generic is defined
     */
    static <E extends MapperEnum> E fromValue(String value, Class<E> clazz) {
        var opEnum = Arrays.stream(clazz.getEnumConstants())
                .filter(e -> value.equals(e.getValue()) || value.equals(((Enum<?>) e).name()))
                .findFirst();

        var generic = MapperEnum.generic(clazz);

        if (null == generic && opEnum.isEmpty()) {
            var op = Arrays.stream(clazz.getEnumConstants()).findFirst();
            String message = op.map(MapperEnum::getErrorMessage).orElse("invalid enum value");
            throw new InvalidValueEnumException("%s - %s".formatted(message, value));
        }

        return opEnum.orElse(clazz.cast(generic));
    }

}
