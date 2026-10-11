package com.herculanoleo.spring.me.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * Registers the same {@link com.herculanoleo.spring.me.converter.web.MapperEnumFormatter}s used by MVC
 * on the reactive conversion service.
 *
 * <p>Loaded only for a reactive application. A servlet application that also has WebFlux on the
 * classpath keeps using MVC until {@code spring.main.web-application-type=reactive}.
 */
@ConditionalOnClass(WebFluxConfigurer.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class MapperEnumWebFluxConfiguration implements WebFluxConfigurer {

    private final MapperResourceLoader mapperResourceLoader;

    public MapperEnumWebFluxConfiguration(MapperResourceLoader mapperResourceLoader) {
        this.mapperResourceLoader = mapperResourceLoader;
    }

    @Override
    public void addFormatters(org.springframework.format.FormatterRegistry registry) {
        MapperEnumRegistrations.register(mapperResourceLoader, registry);
    }

}
