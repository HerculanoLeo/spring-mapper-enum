package com.herculanoleo.spring.me.configuration;

import com.herculanoleo.spring.me.converter.web.MapperEnumFormatterAnnotationFactory;
import com.herculanoleo.spring.me.models.feign.MapperEnumQueryMapEncoder;
import feign.QueryMapEncoder;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.format.FormatterRegistry;

import java.util.HashSet;

public class FeignStartConfiguration implements FeignFormatterRegistrar {

    private final ResourceLoader resourceLoader;

    public FeignStartConfiguration(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {
        var formatters = resourceLoader.serializableEnumFormatter();
        formatters.forEach(registry::addFormatterForFieldType);
        registry.addFormatterForFieldAnnotation(
                new MapperEnumFormatterAnnotationFactory(new HashSet<>(resourceLoader.getClasses()))
        );
    }

    @Bean
    public QueryMapEncoder serializableEnumQueryMapEncoder() {
        return new MapperEnumQueryMapEncoder();
    }

}
