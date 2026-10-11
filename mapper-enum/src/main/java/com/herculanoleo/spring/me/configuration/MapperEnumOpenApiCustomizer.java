package com.herculanoleo.spring.me.configuration;

import com.herculanoleo.spring.me.models.enums.MapperEnum;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springdoc.core.customizers.PropertyCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

/**
 * Publishes {@link MapperEnum#getValue()} codes in the OpenAPI schema when springdoc is present.
 *
 * <p>springdoc passes either a {@link Class} or a Jackson {@code JavaType}. Both expose the enum
 * class used to list {@code getValue()} codes.
 */
@ConditionalOnClass(PropertyCustomizer.class)
public class MapperEnumOpenApiCustomizer implements PropertyCustomizer {

    @Override
    public Schema<?> customize(Schema property, AnnotatedType type) {
        var clazz = rawClass(type);
        if (clazz == null || !clazz.isEnum() || !MapperEnum.class.isAssignableFrom(clazz)) {
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

    private static Class<?> rawClass(AnnotatedType type) {
        if (type == null || type.getType() == null) {
            return null;
        }
        var raw = type.getType();
        if (raw instanceof Class<?> clazz) {
            return clazz;
        }
        if (raw instanceof ParameterizedType parameterized && parameterized.getRawType() instanceof Class<?> clazz) {
            return clazz;
        }
        try {
            var method = raw.getClass().getMethod("getRawClass");
            if (method.invoke(raw) instanceof Class<?> clazz) {
                return clazz;
            }
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
        return null;
    }

}
