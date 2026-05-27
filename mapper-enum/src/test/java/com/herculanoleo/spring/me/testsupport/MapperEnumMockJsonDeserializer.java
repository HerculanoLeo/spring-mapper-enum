package com.herculanoleo.spring.me.testsupport;

import com.herculanoleo.spring.me.models.enums.MapperEnum;
import com.herculanoleo.spring.me.models.enums.MapperEnumMock;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

public final class MapperEnumMockJsonDeserializer extends ValueDeserializer<MapperEnumMock> {

    public static final MapperEnumMockJsonDeserializer INSTANCE = new MapperEnumMockJsonDeserializer();

    private MapperEnumMockJsonDeserializer() {
    }

    @Override
    public MapperEnumMock deserialize(JsonParser jsonParser, DeserializationContext context) {
        if (jsonParser.currentToken() == JsonToken.VALUE_NULL) {
            return null;
        }
        return (MapperEnumMock) MapperEnum.fromValue(jsonParser.getString(), MapperEnumMock.class);
    }
}
