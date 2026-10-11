package com.herculanoleo.spring.me.configuration;

import com.herculanoleo.spring.me.models.feign.MapperEnumQueryMapEncoder;
import feign.QueryMapEncoder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.context.annotation.Bean;

/**
 * Feign configuration imported by {@link com.herculanoleo.spring.me.models.annotation.EnableFeignMapperEnum}
 * and by auto-configuration when OpenFeign is on the classpath.
 *
 * <p>The query map encoder bean is skipped when the application already declared a
 * {@link QueryMapEncoder}.
 */
@ConditionalOnClass(QueryMapEncoder.class)
public class FeignStartConfiguration implements FeignFormatterRegistrar {

    private final MapperResourceLoader mapperResourceLoader;

    public FeignStartConfiguration(MapperResourceLoader mapperResourceLoader) {
        this.mapperResourceLoader = mapperResourceLoader;
    }

    @Override
    public void registerFormatters(org.springframework.format.FormatterRegistry registry) {
        MapperEnumRegistrations.register(mapperResourceLoader, registry);
    }

    /**
     * Feign {@link QueryMapEncoder} that writes {@link com.herculanoleo.spring.me.models.enums.MapperEnum}
     * fields using {@link com.herculanoleo.spring.me.models.enums.MapperEnum#getValue()}.
     *
     * @return the query map encoder bean
     */
    @Bean
    @ConditionalOnMissingBean(QueryMapEncoder.class)
    public QueryMapEncoder serializableEnumQueryMapEncoder() {
        return new MapperEnumQueryMapEncoder();
    }

}
