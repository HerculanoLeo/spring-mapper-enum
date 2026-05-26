package com.herculanoleo.spring.me.models.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Triggers generation of a JPA {@link jakarta.persistence.AttributeConverter} for the annotated enum.
 *
 * <p>When {@code jakarta.persistence} is on the classpath, the annotation processor emits an
 * {@code AttributeConverter} with {@code @Converter(autoApply = true)} that stores
 * {@link com.herculanoleo.spring.me.models.enums.MapperEnum#getValue()} in the database column
 * and reads it back via {@link com.herculanoleo.spring.me.models.enums.MapperEnum#fromValue(String, Class)}.
 *
 * <p>The enum must also be annotated with {@link MapperEnumType} so it is registered for JSON and MVC.
 * Requires {@code spring-data-jpa} (or equivalent) in the consuming application.
 *
 * @see MapperEnumType
 * @see com.herculanoleo.spring.me.processor.GeneratedMapperEnumProcessor
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MapperEnumDBConverter {
}
