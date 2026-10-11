package com.herculanoleo.spring.me.configuration;

import com.herculanoleo.spring.me.models.enums.MapperEnum;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springdoc.core.customizers.PropertyCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import java.util.Arrays;

/**
 * Publishes {@link MapperEnum#getValue()} codes in the OpenAPI schema when springdoc is present.
 */
@ConditionalOnClass(PropertyCustomizer.class)
public class MapperEnumOpenApiCustomizer implements PropertyCustomizer {

    @Override
    public Schema<?> customize(Schema property, AnnotatedType type) {
        var raw = type == null ? null : type.getType();
        if (!(raw instanceof Class<?> clazz) || !clazz.isEnum() || !MapperEnum.class.isAssignableFrom(clazz)) {
            return property;
        }
        var schema = new StringSchema();
        schema.setEnum(Arrays.stream(clazz.getEnumConstants())
                .map(constant -> ((MapperEnum) constant).getValue())
                .toList());
        if (property != null) {
            schema.setName(property.getName());
            schema.setDescription(property.getDescription());
        }
        return schema;
    }

}
