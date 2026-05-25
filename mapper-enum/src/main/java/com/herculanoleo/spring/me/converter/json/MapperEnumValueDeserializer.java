package com.herculanoleo.spring.me.converter.json;

import com.herculanoleo.spring.me.models.enums.MapperEnum;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;

public class MapperEnumValueDeserializer extends ValueDeserializer<MapperEnum> {

    protected JavaType jsonType;

    public MapperEnumValueDeserializer() {
    }

    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) {
        this.jsonType = property.getType();
        return this;
    }

    @Override
    public MapperEnum deserialize(JsonParser jsonParser, DeserializationContext context) {
        if (jsonParser.currentToken() == JsonToken.VALUE_NULL) {
            return null;
        }

        var clazz = this.jsonType.getRawClass();
        var subclass = clazz.asSubclass(MapperEnum.class);

        return MapperEnum.fromValue(jsonParser.getString(), subclass);
    }


}
