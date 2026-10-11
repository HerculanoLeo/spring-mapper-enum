package com.herculanoleo.spring.me.testsupport;

import com.herculanoleo.spring.me.converter.json.MapperEnumJson;
import com.herculanoleo.spring.me.models.enums.MapperEnumMock;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

public final class MapperEnumMockJsonDeserializer extends ValueDeserializer<MapperEnumMock> {

    public static final MapperEnumMockJsonDeserializer INSTANCE = new MapperEnumMockJsonDeserializer();

    private MapperEnumMockJsonDeserializer() {
    }

    @Override
    public MapperEnumMock deserialize(JsonParser jsonParser, DeserializationContext context) {
        return MapperEnumJson.read(jsonParser, context, MapperEnumMock.class);
    }
}
