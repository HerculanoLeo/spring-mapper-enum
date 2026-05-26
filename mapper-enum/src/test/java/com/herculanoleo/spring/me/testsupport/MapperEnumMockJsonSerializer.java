package com.herculanoleo.spring.me.testsupport;

import com.herculanoleo.spring.me.models.enums.MapperEnumMock;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public final class MapperEnumMockJsonSerializer extends ValueSerializer<MapperEnumMock> {

    public static final MapperEnumMockJsonSerializer INSTANCE = new MapperEnumMockJsonSerializer();

    private MapperEnumMockJsonSerializer() {
    }

    @Override
    public void serialize(MapperEnumMock value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        gen.writeString(value.getValue());
    }
}
