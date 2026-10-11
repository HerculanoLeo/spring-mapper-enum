package com.herculanoleo.spring.me.testsupport;

import com.herculanoleo.spring.me.converter.json.MapperEnumJson;
import org.example.test.enums.OutsidePackageMapperMock;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

public final class OutsidePackageMapperMockJsonDeserializer extends ValueDeserializer<OutsidePackageMapperMock> {

    public static final OutsidePackageMapperMockJsonDeserializer INSTANCE = new OutsidePackageMapperMockJsonDeserializer();

    private OutsidePackageMapperMockJsonDeserializer() {
    }

    @Override
    public OutsidePackageMapperMock deserialize(JsonParser jsonParser, DeserializationContext context) {
        return MapperEnumJson.read(jsonParser, context, OutsidePackageMapperMock.class);
    }
}
