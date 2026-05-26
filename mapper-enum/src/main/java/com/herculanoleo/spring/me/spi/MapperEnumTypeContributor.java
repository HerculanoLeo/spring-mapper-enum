package com.herculanoleo.spring.me.spi;

import com.herculanoleo.spring.me.models.enums.MapperEnum;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;

public interface MapperEnumTypeContributor {

    Class<? extends MapperEnum> enumType();

    ValueSerializer<?> serializer();

    ValueDeserializer<?> deserializer();
}
