package com.herculanoleo.spring.me.testsupport;

import com.herculanoleo.spring.me.models.enums.MapperEnum;
import org.example.test.enums.OutsidePackageMapperMock;
import com.herculanoleo.spring.me.spi.MapperEnumTypeContributor;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;

public final class OutsidePackageMapperMockContributor implements MapperEnumTypeContributor {

    @Override
    public Class<? extends MapperEnum> enumType() {
        return OutsidePackageMapperMock.class;
    }

    @Override
    public ValueSerializer<?> serializer() {
        return OutsidePackageMapperMockJsonSerializer.INSTANCE;
    }

    @Override
    public ValueDeserializer<?> deserializer() {
        return OutsidePackageMapperMockJsonDeserializer.INSTANCE;
    }
}
