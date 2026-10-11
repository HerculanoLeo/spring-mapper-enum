package com.herculanoleo.spring.me.models.enums;


import com.herculanoleo.spring.me.models.annotation.MappedEnum;

import java.util.Arrays;
import java.util.Optional;

/**
 * Contract for Java enums that map to a custom string value in HTTP, JSON, Feign, and the database.
 *
 * <p>Implement this interface on every enum you want the library to convert automatically.
 * Register the enum at compile time with {@link MappedEnum} so serializers, deserializers,
 * and Spring formatters are generated and discovered via {@code META-INF/services}
 * (no runtime classpath scanning, which keeps startup predictable and native-image friendly).
 *
 * <p>Resolution uses {@link #getValue()} only. The Java enum constant name is not accepted on
 * HTTP, JSON, or persistence boundaries.
 *
 * <p><strong>Example</strong>
 * <pre>{@code
 * @MappedEnum
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
 * @see MapperEnum#findByValue(String, Class)
 */
public interface MapperEnum {

    int MAX_REPORTED_VALUE_LENGTH = 64;

    /**
     * Returns the string representation of this constant used in APIs and persistence.
     *
     * @return the mapped value (for example {@code "T"} for {@code TaskStatus.TODO})
     */
    String getValue();

    /**
     * Optional fallback constant when {@link #fromValue(String, Class)} receives an unknown value.
     *
     * <p>Override on the first enum constant (convention used by {@link #generic(Class)}) to return
     * the default instance, or return {@code null} to reject unknown values with
     * {@link IllegalArgumentException}. Prefer {@link MappedEnum#fallback()} so the processor
     * can check the constant name at compile time.
     *
     * <p>Unknown input becomes this constant. Use this only when that default is safe.
     * Authorization enums should leave it as {@code null} so unknown input fails.
     *
     * @return the generic constant, or {@code null} if none
     */
    default MapperEnum getGeneric() {
        return null;
    }

    /**
     * Optional message included in {@link IllegalArgumentException} when conversion fails.
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
     * @throws IllegalArgumentException if {@code clazz} is not an enum
     */
    static <E extends MapperEnum> E generic(Class<E> clazz) {
        if (clazz.isEnum()) {
            var generic = Arrays.stream(clazz.getEnumConstants())
                    .findFirst()
                    .map(MapperEnum::getGeneric)
                    .orElse(null);

            return clazz.cast(generic);
        }
        throw new IllegalArgumentException("generic value is not an enum");
    }

    /**
     * Converts a string to an enum constant of the given type using {@link #getValue()} only.
     *
     * <p>{@code null} returns {@code null}. A blank string is not a match unless a fallback is
     * configured. JSON, MVC, WebFlux, and JPA treat a blank string as absence before calling this
     * method, so an empty filter stays empty.
     *
     * @param value the incoming string (JSON body, query parameter, database column, etc.), or {@code null}
     * @param clazz the target enum class
     * @param <E>   the enum type
     * @return the matching constant, the fallback constant, or {@code null} when {@code value} is {@code null}
     * @throws IllegalArgumentException if no constant matches and no fallback is defined
     */
    static <E extends MapperEnum> E fromValue(String value, Class<E> clazz) {
        if (value == null) {
            return null;
        }
        if (!clazz.isEnum()) {
            throw new IllegalArgumentException("target type is not an enum");
        }

        var match = Arrays.stream(clazz.getEnumConstants())
                .filter(e -> value.equals(e.getValue()))
                .findFirst();
        if (match.isPresent()) {
            return match.get();
        }

        var fallback = fallback(clazz);
        if (fallback != null) {
            return fallback;
        }

        String message = Arrays.stream(clazz.getEnumConstants())
                .findFirst()
                .map(MapperEnum::getErrorMessage)
                .filter(candidate -> candidate != null && !candidate.isBlank())
                .orElse("invalid enum value");
        throw new IllegalArgumentException("%s - %s".formatted(message, sanitize(value)));
    }

    /**
     * Looks up an enum constant by {@link #getValue()} without throwing and without using a fallback.
     *
     * <p>Returns {@link Optional#empty()} when {@code value} is {@code null} or no constant matches.
     * Prefer this over {@link #fromValue(String, Class)} when absence is an expected outcome.
     *
     * @param value the incoming string, or {@code null}
     * @param clazz the target enum class
     * @param <E>   the enum type
     * @return the matching constant, or empty if none
     */
    static <E extends MapperEnum> Optional<E> findByValue(String value, Class<E> clazz) {
        if (value == null || !clazz.isEnum()) {
            return Optional.empty();
        }
        return Arrays.stream(clazz.getEnumConstants())
                .filter(e -> value.equals(e.getValue()))
                .findFirst();
    }

    private static <E extends MapperEnum> E fallback(Class<E> clazz) {
        var declared = clazz.getAnnotation(MappedEnum.class);
        if (declared != null && !declared.fallback().isBlank()) {
            for (var constant : clazz.getEnumConstants()) {
                if (declared.fallback().equals(((Enum<?>) constant).name())) {
                    return constant;
                }
            }
            throw new IllegalArgumentException("fallback constant does not exist: " + sanitize(declared.fallback()));
        }
        return generic(clazz);
    }

    private static String sanitize(String value) {
        var cleaned = new StringBuilder(Math.min(value.length(), MAX_REPORTED_VALUE_LENGTH));
        var source = value.length() > MAX_REPORTED_VALUE_LENGTH
                ? value.substring(0, MAX_REPORTED_VALUE_LENGTH)
                : value;
        for (int i = 0; i < source.length(); i++) {
            char current = source.charAt(i);
            if (!Character.isISOControl(current)) {
                cleaned.append(current);
            }
        }
        if (value.length() > MAX_REPORTED_VALUE_LENGTH) {
            cleaned.append("...");
        }
        return cleaned.toString();
    }

}
