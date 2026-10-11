package com.herculanoleo.spring.me.configuration;

import com.herculanoleo.spring.me.models.enums.MapperEnumMock;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class MapperEnumOpenApiCustomizerTest {

    @DisplayName("Should publish mapped values in the OpenAPI schema")
    @Test
    void publishesMappedValues() {
        var customizer = new MapperEnumOpenApiCustomizer();
        Schema<?> schema = customizer.customize(new Schema<>().name("status"), new AnnotatedType(MapperEnumMock.class));

        assertEquals("status", schema.getName());
        assertIterableEquals(java.util.List.of("A", "I"), schema.getEnum());
    }

    @DisplayName("Should publish mapped values when springdoc passes a Jackson JavaType")
    @Test
    void publishesMappedValuesForJacksonType() {
        var customizer = new MapperEnumOpenApiCustomizer();
        var javaType = TypeFactory.defaultInstance().constructType(MapperEnumMock.class);
        Schema<?> schema = customizer.customize(new Schema<>().name("status"), new AnnotatedType(javaType));

        assertEquals("status", schema.getName());
        assertIterableEquals(java.util.List.of("A", "I"), schema.getEnum());
    }
}
