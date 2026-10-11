package com.herculanoleo.spring.me.configuration;

import com.herculanoleo.spring.me.converter.web.MapperEnumPropertyEditor;
import com.herculanoleo.spring.me.models.enums.MapperEnum;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * Registers the same {@link com.herculanoleo.spring.me.converter.web.MapperEnumFormatter}s used by MVC
 * on the reactive conversion service.
 *
 * <p>Loaded only for a reactive application. A servlet application that also has WebFlux on the
 * classpath keeps using MVC until {@code spring.main.web-application-type=reactive}.
 *
 * <p>A custom editor rejects the Java constant name, matching servlet binding.
 */
@ControllerAdvice
@ConditionalOnClass(WebFluxConfigurer.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class MapperEnumWebFluxConfiguration implements WebFluxConfigurer {

    private final MapperResourceLoader mapperResourceLoader;

    public MapperEnumWebFluxConfiguration(MapperResourceLoader mapperResourceLoader) {
        this.mapperResourceLoader = mapperResourceLoader;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        MapperEnumRegistrations.register(mapperResourceLoader, registry);
    }

    @InitBinder
    public void registerMapperEnumEditors(WebDataBinder binder) {
        for (Class<? extends MapperEnum> type : mapperResourceLoader.getClasses()) {
            binder.registerCustomEditor(type, new MapperEnumPropertyEditor(type));
        }
    }

}
