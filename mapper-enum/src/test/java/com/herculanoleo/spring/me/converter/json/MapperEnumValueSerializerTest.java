package com.herculanoleo.spring.me.converter.json;

import com.herculanoleo.spring.me.models.enums.MapperEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MapperEnumValueSerializerTest {

    private enum MockMapperEnum implements MapperEnum {
        GENERIC_1("G_1"),
        GENERIC_2("G_2"),
        ;

        private final String value;

        MockMapperEnum(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return this.value;
        }

        @Override
        public MapperEnum getGeneric() {
            return GENERIC_2;
        }
    }

    @DisplayName("Should call JsonGenerator.writeString with value of MockMapperEnum.GENERIC_1")
    @Test
    public void serializeTest() throws JacksonException {
        var jsonSerializer = new MapperEnumValueSerializer();

        var gen = Mockito.mock(JsonGenerator.class);
        var serializers = Mockito.mock(SerializationContext.class);

        jsonSerializer.serialize(MockMapperEnum.GENERIC_1, gen, serializers);

        verify(gen).writeString(eq(MockMapperEnum.GENERIC_1.getValue()));
    }

    @DisplayName("Should throw JacksonException when fail to execute JsonGenerator.writeString")
    @Test
    public void serializeJacksonExceptionTest() throws JacksonException {
        var jsonSerializer = new MapperEnumValueSerializer();

        var gen = Mockito.mock(JsonGenerator.class);
        doThrow(JacksonException.class).when(gen).writeString(eq(MockMapperEnum.GENERIC_2.getValue()));

        var serializers = Mockito.mock(SerializationContext.class);

        assertThrows(JacksonException.class, () -> jsonSerializer.serialize(MockMapperEnum.GENERIC_2, gen, serializers));
    }

}
