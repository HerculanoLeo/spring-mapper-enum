package com.herculanoleo.spring.me.models.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Opt-in formatter for a specific {@link com.herculanoleo.spring.me.models.enums.MapperEnum} field or parameter.
 *
 * <p>By default, {@link EnableMapperEnum} registers formatters per enum type for all fields of that type.
 * Use this annotation when you need explicit formatting on a single field, method parameter, or getter
 * without relying on the declared field type alone.
 *
 * <p>Handled by {@link com.herculanoleo.spring.me.converter.web.MapperEnumFormatterAnnotationFactory}.
 *
 * @see EnableMapperEnum
 * @see com.herculanoleo.spring.me.converter.web.MapperEnumFormatter
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
public @interface MapperEnumFormat {
}
