package com.herculanoleo.spring.me.configuration;

import com.herculanoleo.spring.me.converter.json.MapperEnumValueDeserializer;
import com.herculanoleo.spring.me.models.enums.MapperEnum;
import com.herculanoleo.spring.me.spi.MapperEnumTypeContributor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.module.SimpleModule;

/**
 * Spring configuration imported by {@link com.herculanoleo.spring.me.models.annotation.EnableMapperEnum}
 * and by auto-configuration.
 *
 * <p>Exposes the {@code mapperEnumModule} Jackson module. Servlet and reactive formatters live in
 * their own configuration classes, so a Jackson and JPA application can start without MVC.
 */
@ConditionalOnClass(JacksonModule.class)
public class StartConfiguration {

    private final MapperResourceLoader mapperResourceLoader;

    public StartConfiguration(MapperResourceLoader mapperResourceLoader) {
        this.mapperResourceLoader = mapperResourceLoader;
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
