package com.herculanoleo.spring.me.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registers mapper enum formatters on the servlet conversion service.
 *
 * <p>Spring Boot 4 applies {@link WebMvcConfigurer#addFormatters} on {@code mvcConversionService}.
 * A {@code FormatterRegistrar} bean is not picked up by that path.
 */
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class MapperEnumWebMvcConfiguration implements WebMvcConfigurer {

    private final MapperResourceLoader mapperResourceLoader;

    public MapperEnumWebMvcConfiguration(MapperResourceLoader mapperResourceLoader) {
        this.mapperResourceLoader = mapperResourceLoader;
    }

    @Override
    public void addFormatters(org.springframework.format.FormatterRegistry registry) {
        MapperEnumRegistrations.register(mapperResourceLoader, registry);
    }

}
