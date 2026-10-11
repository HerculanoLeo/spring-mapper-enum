package com.herculanoleo.spring.me.models.annotation;

import com.herculanoleo.spring.me.configuration.FeignStartConfiguration;
import com.herculanoleo.spring.me.models.feign.MapperEnumQueryMapEncoder;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables Feign-specific support for {@link com.herculanoleo.spring.me.models.enums.MapperEnum} types.
 *
 * <p>Imports {@link com.herculanoleo.spring.me.configuration.FeignStartConfiguration}, which:
 * <ul>
 *   <li>Registers the same MVC formatters as {@link EnableMapperEnum} on the Feign
 *       {@link org.springframework.format.FormatterRegistry}</li>
 *   <li>Exposes a {@link MapperEnumQueryMapEncoder} bean so {@code @SpringQueryMap} objects
 *       encode enum fields with {@link com.herculanoleo.spring.me.models.enums.MapperEnum#getValue()}
 *       instead of {@link Object#toString()}</li>
 * </ul>
 *
 * <p>{@link com.herculanoleo.spring.me.configuration.MapperResourceLoader} comes from auto-configuration.
 * Requires {@code spring-cloud-starter-openfeign} on the classpath. When the application already
 * defines a {@code QueryMapEncoder} bean, that bean is kept.
 *
 * @see EnableMapperEnum
 * @see MapperEnumQueryMapEncoder
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(FeignStartConfiguration.class)
public @interface EnableFeignMapperEnum {
}
