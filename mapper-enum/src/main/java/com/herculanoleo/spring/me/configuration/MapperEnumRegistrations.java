package com.herculanoleo.spring.me.configuration;

import com.herculanoleo.spring.me.converter.web.MapperEnumFormatterAnnotationFactory;
import org.springframework.format.FormatterRegistry;

import java.util.HashSet;

/**
 * Registers {@link com.herculanoleo.spring.me.converter.web.MapperEnumFormatter}s on a Spring
 * {@link FormatterRegistry}. Shared by MVC, WebFlux, and Feign.
 */
public final class MapperEnumRegistrations {

    private MapperEnumRegistrations() {
    }

    /**
     * Adds one formatter per registered enum type and the {@code @MapperEnumFormat} factory.
     *
     * @param loader   the loaded enum types
     * @param registry the MVC, WebFlux, or Feign formatter registry
     */
    public static void register(MapperResourceLoader loader, FormatterRegistry registry) {
        loader.serializableEnumFormatter().forEach(registry::addFormatterForFieldType);
        registry.addFormatterForFieldAnnotation(
                new MapperEnumFormatterAnnotationFactory(new HashSet<>(loader.getClasses()))
        );
    }

}
