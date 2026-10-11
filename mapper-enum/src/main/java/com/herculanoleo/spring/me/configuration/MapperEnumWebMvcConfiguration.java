package com.herculanoleo.spring.me.configuration;

import com.herculanoleo.spring.me.converter.web.MapperEnumPropertyEditor;
import com.herculanoleo.spring.me.models.enums.MapperEnum;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registers mapper enum formatters on the servlet conversion service.
 *
 * <p>Spring Boot 4 applies {@link WebMvcConfigurer#addFormatters} on {@code mvcConversionService}.
 * A {@code FormatterRegistrar} bean is not picked up by that path.
 *
 * <p>A custom editor is also registered. Without it, a failed formatter falls back to
 * {@link Enum#valueOf(Class, String)} and accepts the constant name.
 */
@ControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class MapperEnumWebMvcConfiguration implements WebMvcConfigurer {

    private final MapperResourceLoader mapperResourceLoader;

    public MapperEnumWebMvcConfiguration(MapperResourceLoader mapperResourceLoader) {
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
