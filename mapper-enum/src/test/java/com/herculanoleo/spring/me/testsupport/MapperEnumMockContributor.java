package com.herculanoleo.spring.me.testsupport;

import com.herculanoleo.spring.me.models.enums.MapperEnum;
import com.herculanoleo.spring.me.models.enums.MapperEnumMock;
import com.herculanoleo.spring.me.spi.MapperEnumTypeContributor;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;

public final class MapperEnumMockContributor implements MapperEnumTypeContributor {

    @Override
    public Class<? extends MapperEnum> enumType() {
        return MapperEnumMock.class;
    }

    @Override
    public ValueSerializer<?> serializer() {
        return MapperEnumMockJsonSerializer.INSTANCE;
    }

    @Override
    public ValueDeserializer<?> deserializer() {
        return MapperEnumMockJsonDeserializer.INSTANCE;
    }
}
