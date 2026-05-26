package com.herculanoleo.spring.me.configuration;

import com.herculanoleo.spring.me.converter.web.MapperEnumFormatterAnnotationFactory;
import com.herculanoleo.spring.me.models.feign.MapperEnumQueryMapEncoder;
import feign.QueryMapEncoder;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.format.FormatterRegistry;

import java.util.HashSet;

/**
 * Feign configuration imported by {@link com.herculanoleo.spring.me.models.annotation.EnableFeignMapperEnum}.
 *
 * <p>Registers the same {@link com.herculanoleo.spring.me.converter.web.MapperEnumFormatter}s as
 * {@link StartConfiguration} on Feign's formatter registry and provides a
 * {@link com.herculanoleo.spring.me.models.feign.MapperEnumQueryMapEncoder} bean.
 *
 * <p>Do not import this class directly; use {@link com.herculanoleo.spring.me.models.annotation.EnableFeignMapperEnum}.
 */
public class FeignStartConfiguration implements FeignFormatterRegistrar {

    private final MapperResourceLoader mapperResourceLoader;

    public FeignStartConfiguration(MapperResourceLoader mapperResourceLoader) {
        this.mapperResourceLoader = mapperResourceLoader;
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {
        var formatters = mapperResourceLoader.serializableEnumFormatter();
        formatters.forEach(registry::addFormatterForFieldType);
        registry.addFormatterForFieldAnnotation(
                new MapperEnumFormatterAnnotationFactory(new HashSet<>(mapperResourceLoader.getClasses()))
        );
    }

    /**
     * Feign {@link QueryMapEncoder} that writes {@link com.herculanoleo.spring.me.models.enums.MapperEnum}
     * fields using {@link com.herculanoleo.spring.me.models.enums.MapperEnum#getValue()}.
     *
     * @return the query map encoder bean
     */
    @Bean
    public QueryMapEncoder serializableEnumQueryMapEncoder() {
        return new MapperEnumQueryMapEncoder();
    }

}
