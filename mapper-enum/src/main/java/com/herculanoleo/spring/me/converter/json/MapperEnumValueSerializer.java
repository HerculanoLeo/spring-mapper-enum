package com.herculanoleo.spring.me.converter.json;

import com.herculanoleo.spring.me.models.enums.MapperEnum;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * Jackson serializer that writes a {@link MapperEnum} as its {@link MapperEnum#getValue()} string.
 *
 * <p>Used by generated per-enum serializers and as a reference implementation. The singleton
 * {@link #INSTANCE} can be reused where a shared serializer is needed.
 */
public class MapperEnumValueSerializer extends ValueSerializer<MapperEnum> {

    /** Shared serializer instance. */
    public static final MapperEnumValueSerializer INSTANCE = new MapperEnumValueSerializer();

    @Override
    public void serialize(MapperEnum value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        gen.writeString(value.getValue());
    }
}
