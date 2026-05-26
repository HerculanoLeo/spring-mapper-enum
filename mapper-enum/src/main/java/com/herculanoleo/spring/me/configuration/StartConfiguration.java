package com.herculanoleo.spring.me.configuration;

import com.herculanoleo.spring.me.converter.json.MapperEnumValueDeserializer;
import com.herculanoleo.spring.me.converter.web.MapperEnumFormatterAnnotationFactory;
import com.herculanoleo.spring.me.models.enums.MapperEnum;
import com.herculanoleo.spring.me.spi.MapperEnumTypeContributor;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.format.support.FormattingConversionService;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.module.SimpleModule;

import java.util.HashSet;

public class StartConfiguration {

    private final FormattingConversionService conversionService;

    private final MapperResourceLoader mapperResourceLoader;

    public StartConfiguration(FormattingConversionService conversionService, MapperResourceLoader mapperResourceLoader) {
        this.conversionService = conversionService;
        this.mapperResourceLoader = mapperResourceLoader;
    }

    @PostConstruct
    public void setup() {
        var formatters = mapperResourceLoader.serializableEnumFormatter();
        formatters.forEach(conversionService::addFormatterForFieldType);

        var classes = mapperResourceLoader.getClasses();

        conversionService.addFormatterForFieldAnnotation(
                new MapperEnumFormatterAnnotationFactory(new HashSet<>(classes))
        );
    }

    @Bean
    @SuppressWarnings({"unchecked", "rawtypes"})
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
                .addSerializer(clazz, (tools.jackson.databind.ValueSerializer) contributor.serializer())
                .addDeserializer(clazz, (tools.jackson.databind.ValueDeserializer) contributor.deserializer());
    }
}
