package com.herculanoleo.spring.me.converter.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.herculanoleo.spring.me.models.enums.MapperEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MapperEnumJsonDeserializerTest {

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

    @DisplayName("Should instantiate a MapperEnumJsonDeserializer with MockMapperEnum JsonType and jsonType is not null")
    @Test
    public void constructorTest() {
        var expectedJsonType = TypeFactory.defaultInstance().constructType(MockMapperEnum.class);
        BeanProperty beanProperty = mock(BeanProperty.class);
        when(beanProperty.getType()).thenReturn(expectedJsonType);

        var jsonDeserializer = new MapperEnumJsonDeserializer();
        jsonDeserializer.createContextual(mock(DeserializationContext.class), beanProperty);

        assertEquals(expectedJsonType, jsonDeserializer.jsonType);
    }

    @DisplayName("Should instantiate a MapperEnumJsonDeserializer with default constructor and jsonType is null")
    @Test
    public void defaultConstructorTest() {
        var jsonDeserializer = new MapperEnumJsonDeserializer();
        assertNull(jsonDeserializer.jsonType);
    }

    @DisplayName("Should return the Enum of the value that came from Json")
    @Test
    public void deserializeTest() throws IOException {
        var expectedJsonType = TypeFactory.defaultInstance().constructType(MockMapperEnum.class);
        BeanProperty beanProperty = mock(BeanProperty.class);
        when(beanProperty.getType()).thenReturn(expectedJsonType);

        var jsonDeserializer = new MapperEnumJsonDeserializer();
        jsonDeserializer.createContextual(mock(DeserializationContext.class), beanProperty);

        var jsonParser = Mockito.mock(JsonParser.class);
        when(jsonParser.getText()).thenReturn(MockMapperEnum.GENERIC_1.getValue());
        var context = Mockito.mock(DeserializationContext.class);

        var result = jsonDeserializer.deserialize(jsonParser, context);

        assertEquals(MockMapperEnum.GENERIC_1, result);
    }

    @DisplayName("Should throw IOException when failed to get text from JsonParser")
    @Test
    public void deserializeIOExceptionTest() throws IOException {
        var expectedJsonType = TypeFactory.defaultInstance().constructType(MockMapperEnum.class);
        BeanProperty beanProperty = mock(BeanProperty.class);
        when(beanProperty.getType()).thenReturn(expectedJsonType);

        var jsonDeserializer = new MapperEnumJsonDeserializer();
        jsonDeserializer.createContextual(mock(DeserializationContext.class), beanProperty);

        var jsonParser = mock(JsonParser.class);
        when(jsonParser.getText()).thenThrow(IOException.class);
        var context = Mockito.mock(DeserializationContext.class);

        assertThrows(IOException.class, () -> jsonDeserializer.deserialize(jsonParser, context));
    }

    @DisplayName("Should return null value when currentToken is a VALUE_NULL")
    @Test
    public void deserializeVALUE_NULLTest() throws IOException {
        var jsonType = TypeFactory.defaultInstance().constructType(MockMapperEnum.class);
        BeanProperty beanProperty = mock(BeanProperty.class);
        when(beanProperty.getType()).thenReturn(jsonType);

        var jsonDeserializer = new MapperEnumJsonDeserializer();
        jsonDeserializer.createContextual(mock(DeserializationContext.class), beanProperty);

        var jsonParser = Mockito.mock(JsonParser.class);
        when(jsonParser.currentToken()).thenReturn(JsonToken.VALUE_NULL);

        var context = Mockito.mock(DeserializationContext.class);

        var result = jsonDeserializer.deserialize(jsonParser, context);

        assertNull(result);
    }


    @DisplayName("Should return a instance of MapperEnumJsonDeserializer with MockMapperEnum JsonType")
    @Test
    public void createContextualTest() {
        var expectedJsonType = TypeFactory.defaultInstance().constructType(MockMapperEnum.class);
        var contextMock = Mockito.mock(DeserializationContext.class);
        var propertyMock = Mockito.mock(BeanProperty.class);

        when(propertyMock.getType()).thenReturn(expectedJsonType);

        var jsonDeserializer = new MapperEnumJsonDeserializer().createContextual(contextMock, propertyMock);

        assertInstanceOf(MapperEnumJsonDeserializer.class, jsonDeserializer);
        assertEquals(expectedJsonType, ((MapperEnumJsonDeserializer) jsonDeserializer).jsonType);
    }


}
