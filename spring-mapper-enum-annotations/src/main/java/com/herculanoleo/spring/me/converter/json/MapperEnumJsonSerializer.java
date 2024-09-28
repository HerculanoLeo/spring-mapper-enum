package com.herculanoleo.spring.me.converter.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.herculanoleo.spring.me.models.enums.MapperEnum;

import java.io.IOException;

public class MapperEnumJsonSerializer extends JsonSerializer<MapperEnum> {
    @Override
    public void serialize(MapperEnum value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.getValue());
    }
}
