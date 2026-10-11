package com.herculanoleo.spring.me.models.annotation;

import com.herculanoleo.spring.me.configuration.MapperEnumImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Documents that the application uses Spring Mapper Enum.
 *
 * <p>Auto-configuration already loads the library when it is on the classpath. This annotation
 * imports the same configuration, so an application that excludes auto-configuration can still
 * opt in. Servlet formatters, the Jackson module, Feign, and WebFlux each load only when the
 * matching stack is present.
 *
 * <p><strong>Prerequisites</strong>
 * <ul>
 *   <li>Enums must implement {@link com.herculanoleo.spring.me.models.enums.MapperEnum}</li>
 *   <li>Enums must be annotated with {@link MappedEnum}</li>
 *   <li>{@code spring-mapper-enum-processor} must run during the application build</li>
 * </ul>
 *
 * @see MappedEnum
 * @see EnableFeignMapperEnum
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(MapperEnumImportSelector.class)
public @interface EnableMapperEnum {
}
