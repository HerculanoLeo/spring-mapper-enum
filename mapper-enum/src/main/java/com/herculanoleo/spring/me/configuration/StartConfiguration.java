package com.herculanoleo.spring.me.configuration;

import com.herculanoleo.spring.me.converter.json.MapperEnumValueDeserializer;
import com.herculanoleo.spring.me.converter.web.MapperEnumFormatterAnnotationFactory;
import com.herculanoleo.spring.me.models.enums.MapperEnum;
import com.herculanoleo.spring.me.spi.MapperEnumTypeContributor;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.format.support.FormattingConversionService;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.module.SimpleModule;

import java.util.HashSet;

/**
 * Spring configuration imported by {@link com.herculanoleo.spring.me.models.annotation.EnableMapperEnum}.
 *
 * <p>At startup, registers MVC formatters for every compile-time registered
 * {@link com.herculanoleo.spring.me.models.enums.MapperEnum} and exposes the
 * {@code mapperEnumModule} Jackson module bean with generated serializers and deserializers.
 *
 * <p>Do not import this class directly; use {@link com.herculanoleo.spring.me.models.annotation.EnableMapperEnum}.
 */
public class StartConfiguration {

    private final FormattingConversionService conversionService;

    private final MapperResourceLoader mapperResourceLoader;

    public StartConfiguration(FormattingConversionService conversionService, MapperResourceLoader mapperResourceLoader) {
        this.conversionService = conversionService;
        this.mapperResourceLoader = mapperResourceLoader;
    }

    /**
     * Registers {@link com.herculanoleo.spring.me.converter.web.MapperEnumFormatter}s on the
     * application {@link org.springframework.format.support.FormattingConversionService}.
     */
    @PostConstruct
    public void setup() {
        var formatters = mapperResourceLoader.serializableEnumFormatter();
        formatters.forEach(conversionService::addFormatterForFieldType);

        var classes = mapperResourceLoader.getClasses();

        conversionService.addFormatterForFieldAnnotation(
                new MapperEnumFormatterAnnotationFactory(new HashSet<>(classes))
        );
    }

    /**
     * Jackson module with a fallback {@link com.herculanoleo.spring.me.converter.json.MapperEnumValueDeserializer}
     * for {@link com.herculanoleo.spring.me.models.enums.MapperEnum} and per-type serializers/deserializers
     * from {@link com.herculanoleo.spring.me.spi.MapperEnumTypeContributor} instances.
     *
     * @return the module registered automatically by Spring Boot Jackson auto-configuration
     */
    @Bean
    public JacksonModule mapperEnumModule() {
        var module = new SimpleModule();

        module = module.addDeserializer(MapperEnum.class, new MapperEnumValueDeserializer());

        for (var contributor : mapperResourceLoader.getContributors()) {
            module = registerContributor(module, contributor);
        }

        return module;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private SimpleModule registerContributor(SimpleModule module, MapperEnumTypeContributor contributor) {
        Class<? extends MapperEnum> clazz = contributor.enumType();
        return module
                .addSerializer(clazz, (ValueSerializer) contributor.serializer())
                .addDeserializer(clazz, (ValueDeserializer) contributor.deserializer());
    }
}
