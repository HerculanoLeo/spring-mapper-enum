package com.herculanoleo.spring.me.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.herculanoleo.spring.me.converter.json.MapperEnumJsonDeserializer;
import com.herculanoleo.spring.me.converter.json.MapperEnumJsonSerializer;
import com.herculanoleo.spring.me.converter.web.MapperEnumFormatterAnnotationFactory;
import com.herculanoleo.spring.me.models.enums.MapperEnum;
import jakarta.annotation.PostConstruct;
import org.springframework.format.support.FormattingConversionService;

import java.util.HashSet;

public class StartConfiguration {

    private final FormattingConversionService conversionService;

    private final MapperResourceLoader mapperResourceLoader;

    private final ObjectMapper objectMapper;

    public StartConfiguration(FormattingConversionService conversionService, MapperResourceLoader mapperResourceLoader, ObjectMapper objectMapper) {
        this.conversionService = conversionService;
        this.mapperResourceLoader = mapperResourceLoader;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void setup() {
        var formatters = mapperResourceLoader.serializableEnumFormatter();
        formatters.forEach(conversionService::addFormatterForFieldType);

        var classes = mapperResourceLoader.getClasses();

        conversionService.addFormatterForFieldAnnotation(
                new MapperEnumFormatterAnnotationFactory(new HashSet<>(classes))
        );

        SimpleModule module = new SimpleModule();

        module = module
                .addDeserializer(MapperEnum.class, new MapperEnumJsonDeserializer());
        for (var clazz : classes) {
            module = module
                    .addSerializer(clazz, new MapperEnumJsonSerializer())
                    .addDeserializer((Class<MapperEnum>) clazz, new MapperEnumJsonDeserializer());
        }

        objectMapper.registerModule(module);
    }
}
