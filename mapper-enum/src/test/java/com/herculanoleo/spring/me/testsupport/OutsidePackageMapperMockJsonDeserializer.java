package com.herculanoleo.spring.me.testsupport;

import com.herculanoleo.spring.me.models.enums.MapperEnum;
import org.example.test.enums.OutsidePackageMapperMock;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

public final class OutsidePackageMapperMockJsonDeserializer extends ValueDeserializer<OutsidePackageMapperMock> {

    public static final OutsidePackageMapperMockJsonDeserializer INSTANCE = new OutsidePackageMapperMockJsonDeserializer();

    private OutsidePackageMapperMockJsonDeserializer() {
    }

    @Override
    public OutsidePackageMapperMock deserialize(JsonParser jsonParser, DeserializationContext context) {
        if (jsonParser.currentToken() == JsonToken.VALUE_NULL) {
            return null;
        }
        return (OutsidePackageMapperMock) MapperEnum.fromValue(jsonParser.getString(), OutsidePackageMapperMock.class);
    }
}
