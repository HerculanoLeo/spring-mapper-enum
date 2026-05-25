package com.herculanoleo.spring.me.converter.json;

import com.herculanoleo.spring.me.models.enums.MapperEnum;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class MapperEnumValueSerializer extends ValueSerializer<MapperEnum> {

    public static final MapperEnumValueSerializer INSTANCE = new MapperEnumValueSerializer();

    @Override
    public void serialize(MapperEnum value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        gen.writeString(value.getValue());
    }
}
