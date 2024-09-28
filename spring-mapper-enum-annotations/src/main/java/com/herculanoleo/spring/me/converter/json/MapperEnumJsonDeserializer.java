package com.herculanoleo.spring.me.converter.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.herculanoleo.spring.me.models.enums.MapperEnum;

import java.io.IOException;

public class MapperEnumJsonDeserializer extends JsonDeserializer<MapperEnum> implements ContextualDeserializer {

    protected JavaType jsonType;

    public MapperEnumJsonDeserializer() {
    }

    @Override
    public MapperEnum deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        if (jsonParser.currentToken() == JsonToken.VALUE_NULL) {
            return null;
        }

        var clazz = this.jsonType.getRawClass();
        var subclass = clazz.asSubclass(MapperEnum.class);

        return MapperEnum.fromValue(jsonParser.getText(), subclass);
    }

    @Override
    public JsonDeserializer<? extends MapperEnum> createContextual(DeserializationContext context, BeanProperty property) {
        this.jsonType = property.getType();
        return this;
    }

}
