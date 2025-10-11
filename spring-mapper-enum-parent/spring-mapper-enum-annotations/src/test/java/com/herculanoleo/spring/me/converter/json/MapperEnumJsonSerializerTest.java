package com.herculanoleo.spring.me.converter.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.herculanoleo.spring.me.models.enums.MapperEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MapperEnumJsonSerializerTest {

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
    public void serializeTest() throws IOException {
        var jsonSerializer = new MapperEnumJsonSerializer();

        var gen = Mockito.mock(JsonGenerator.class);
        var serializers = Mockito.mock(SerializerProvider.class);

        jsonSerializer.serialize(MockMapperEnum.GENERIC_1, gen, serializers);

        verify(gen).writeString(eq(MockMapperEnum.GENERIC_1.getValue()));
    }

    @DisplayName("Should throw IOException when fail to execute JsonGenerator.writeString")
    @Test
    public void serializeIOExceptionTest() throws IOException {
        var jsonSerializer = new MapperEnumJsonSerializer();

        var gen = Mockito.mock(JsonGenerator.class);
        doThrow(IOException.class).when(gen).writeString(eq(MockMapperEnum.GENERIC_2.getValue()));

        var serializers = Mockito.mock(SerializerProvider.class);

        assertThrows(IOException.class, () -> jsonSerializer.serialize(MockMapperEnum.GENERIC_2, gen, serializers));
    }

}
