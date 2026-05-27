package com.herculanoleo.spring.me.models.annotation;

import com.herculanoleo.spring.me.configuration.MapperResourceLoader;
import com.herculanoleo.spring.me.configuration.StartConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables Spring Mapper Enum for the application.
 *
 * <p>Add this annotation to your {@code @SpringBootApplication} class or any {@code @Configuration}
 * type. It imports {@link com.herculanoleo.spring.me.configuration.StartConfiguration} and
 * {@link com.herculanoleo.spring.me.configuration.MapperResourceLoader}, which:
 * <ul>
 *   <li>Load {@link com.herculanoleo.spring.me.spi.MapperEnumTypeContributor} implementations
 *       from {@code META-INF/services} (generated at compile time for each {@link MapperEnumType}
 *       enum)</li>
 *   <li>Register Spring MVC {@link com.herculanoleo.spring.me.converter.web.MapperEnumFormatter}s
 *       for request parameters and path variables</li>
 *   <li>Register a Jackson module ({@code mapperEnumModule}) with type-specific JSON
 *       serializers and deserializers</li>
 * </ul>
 *
 * <p><strong>Prerequisites</strong>
 * <ul>
 *   <li>Enums must implement {@link com.herculanoleo.spring.me.models.enums.MapperEnum}</li>
 *   <li>Enums must be annotated with {@link MapperEnumType}</li>
 *   <li>{@code spring-mapper-enum} must be on {@code annotationProcessorPaths} during the application build</li>
 * </ul>
 *
 * <p>For OpenFeign support, also add {@link EnableFeignMapperEnum}.
 *
 * @see MapperEnumType
 * @see EnableFeignMapperEnum
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({StartConfiguration.class, MapperResourceLoader.class})
public @interface EnableMapperEnum {

    /**
     * Reserved; enum discovery is compile-time via {@link MapperEnumType}, not runtime scanning.
     */
    String[] value() default {};

    /**
     * Reserved; enum discovery is compile-time via {@link MapperEnumType}, not runtime scanning.
     */
    String[] basePackages() default {};

}
