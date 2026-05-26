package com.herculanoleo.spring.me.models.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a {@link com.herculanoleo.spring.me.models.enums.MapperEnum} for compile-time registration
 * (JSON, MVC formatters). Use together with {@link MapperEnumDBConverter} when JPA persistence is required.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MapperEnumType {
}
