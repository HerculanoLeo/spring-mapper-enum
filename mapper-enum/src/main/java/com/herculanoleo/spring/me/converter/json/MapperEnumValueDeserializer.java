package com.herculanoleo.spring.me.converter.json;

import com.herculanoleo.spring.me.models.enums.MapperEnum;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;

/**
 * Fallback Jackson deserializer for properties declared as {@link MapperEnum} without a concrete enum type.
 *
 * <p>When used as a contextual deserializer (via {@link #createContextual}), resolves the concrete
 * enum type from the bean property and delegates to {@link MapperEnum#fromValue(String, Class)}.
 *
 * <p>For compile-time registered enums, prefer the generated type-specific deserializers registered
 * through {@link com.herculanoleo.spring.me.spi.MapperEnumTypeContributor}.
 */
public class MapperEnumValueDeserializer extends ValueDeserializer<MapperEnum> {

    protected final JavaType jsonType;

    public MapperEnumValueDeserializer() {
        this.jsonType = null;
    }

    private MapperEnumValueDeserializer(JavaType jsonType) {
        this.jsonType = jsonType;
    }

    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) {
        if (property == null) {
            return this;
        }
        return new MapperEnumValueDeserializer(property.getType());
    }

    @Override
    public MapperEnum deserialize(JsonParser jsonParser, DeserializationContext context) {
        if (jsonParser.currentToken() == JsonToken.VALUE_NULL || null == this.jsonType) {
            return null;
        }

        var clazz = this.jsonType.getRawClass();
        var subclass = clazz.asSubclass(MapperEnum.class);

        return MapperEnum.fromValue(jsonParser.getString(), subclass);
    }

}
