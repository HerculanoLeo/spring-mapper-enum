package com.herculanoleo.spring.me.spi;

import com.herculanoleo.spring.me.models.annotation.MapperEnumType;
import com.herculanoleo.spring.me.models.enums.MapperEnum;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;

/**
 * Service provider that binds a registered {@link MapperEnum} type to its Jackson adapters.
 *
 * <p>Implementations are generated at compile time for each {@link MapperEnumType} enum and listed in
 * {@code META-INF/services/com.herculanoleo.spring.me.spi.MapperEnumTypeContributor}.
 * {@link com.herculanoleo.spring.me.configuration.MapperResourceLoader} loads them at startup.
 *
 * <p>Application code should not implement this interface manually unless you have a special
 * registration requirement; prefer {@link MapperEnumType} on your enum instead.
 */
public interface MapperEnumTypeContributor {

    /**
     * The enum type contributed by this provider.
     *
     * @return the {@link MapperEnum} class
     */
    Class<? extends MapperEnum> enumType();

    /**
     * Jackson serializer that writes {@link MapperEnum#getValue()} as a JSON string.
     *
     * @return the serializer instance (typically a singleton {@code INSTANCE} field)
     */
    ValueSerializer<?> serializer();

    /**
     * Jackson deserializer that reads a JSON string and resolves it via
     * {@link MapperEnum#fromValue(String, Class)}.
     *
     * @return the deserializer instance (typically a singleton {@code INSTANCE} field)
     */
    ValueDeserializer<?> deserializer();
}
