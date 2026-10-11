package com.herculanoleo.spring.me.models.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Registers a {@link com.herculanoleo.spring.me.models.enums.MapperEnum} at compile time.
 *
 * <p>When this annotation is present on an enum, the annotation processor generates:
 * <ul>
 *   <li>Type-specific Jackson serializer and deserializer classes</li>
 *   <li>A {@link com.herculanoleo.spring.me.spi.MapperEnumTypeContributor} implementation</li>
 *   <li>A {@code META-INF/services/com.herculanoleo.spring.me.spi.MapperEnumTypeContributor}
 *       entry for {@link java.util.ServiceLoader} discovery at startup</li>
 * </ul>
 *
 * <p>This approach avoids runtime classpath scanning and works well with GraalVM native images,
 * provided the annotation processor runs in the application build.
 *
 * <p>Use together with {@link MapperEnumDBConverter} when the enum is persisted with JPA.
 *
 * @see com.herculanoleo.spring.me.models.annotation.EnableMapperEnum
 * @see MapperEnumDBConverter
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MapperEnumType {

    /**
     * Enum constant name used when an incoming value does not match {@code getValue()}.
     *
     * <p>Empty means there is no annotation-level fallback. Unknown values then use
     * {@link com.herculanoleo.spring.me.models.enums.MapperEnum#getGeneric()} when that method is
     * overridden, and otherwise fail with {@link IllegalArgumentException}.
     *
     * <p>A fallback maps unknown input onto a real constant. Do not set this for enums used in
     * authorization.
     *
     * @return the simple name of a constant of this enum, or an empty string
     */
    String fallback() default "";

}
