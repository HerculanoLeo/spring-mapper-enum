package com.herculanoleo.spring.me.models.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Triggers generation of a JPA {@code AttributeConverter} for the annotated enum.
 *
 * <p>When {@code jakarta.persistence} is on the classpath, the annotation processor emits an
 * {@code AttributeConverter} whose {@code @Converter(autoApply)} follows {@link #autoApply()} that stores
 * {@link com.herculanoleo.spring.me.models.enums.MapperEnum#getValue()} in the database column
 * and reads it back via {@link com.herculanoleo.spring.me.models.enums.MapperEnum#fromValue(String, Class)}.
 *
 * <p>The enum must also be annotated with {@link MapperEnumType} so it is registered for JSON and MVC.
 * Requires {@code spring-data-jpa} (or equivalent) in the consuming application.
 *
 * @see MapperEnumType
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MapperEnumDBConverter {

    /**
     * Value copied to the generated {@code @Converter(autoApply)} attribute.
     *
     * @return {@code true} to apply the converter to every field of this enum type
     */
    boolean autoApply() default true;

}
