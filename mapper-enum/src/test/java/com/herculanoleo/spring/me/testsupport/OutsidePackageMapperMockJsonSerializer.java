package com.herculanoleo.spring.me.testsupport;

import org.example.test.enums.OutsidePackageMapperMock;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public final class OutsidePackageMapperMockJsonSerializer extends ValueSerializer<OutsidePackageMapperMock> {

    public static final OutsidePackageMapperMockJsonSerializer INSTANCE = new OutsidePackageMapperMockJsonSerializer();

    private OutsidePackageMapperMockJsonSerializer() {
    }

    @Override
    public void serialize(OutsidePackageMapperMock value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        gen.writeString(value.getValue());
    }
}
