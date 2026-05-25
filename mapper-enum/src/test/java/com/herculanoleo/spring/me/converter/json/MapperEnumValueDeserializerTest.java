package com.herculanoleo.spring.me.converter.json;

import com.herculanoleo.spring.me.models.enums.MapperEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.type.TypeFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MapperEnumValueDeserializerTest {

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

    @DisplayName("Should instantiate a MapperEnumValueDeserializer with MockMapperEnum JsonType and jsonType is not null")
    @Test
    public void constructorTest() {
        var expectedJsonType = TypeFactory.createDefaultInstance().constructType(MockMapperEnum.class);
        BeanProperty beanProperty = mock(BeanProperty.class);
        when(beanProperty.getType()).thenReturn(expectedJsonType);

        var jsonDeserializer = (MapperEnumValueDeserializer) new MapperEnumValueDeserializer()
                .createContextual(mock(DeserializationContext.class), beanProperty);

        assertEquals(expectedJsonType, jsonDeserializer.jsonType);
    }

    @DisplayName("Should instantiate a MapperEnumValueDeserializer with default constructor and jsonType is null")
    @Test
    public void defaultConstructorTest() {
        var jsonDeserializer = new MapperEnumValueDeserializer();
        assertNull(jsonDeserializer.jsonType);
    }

    @DisplayName("Should return the Enum of the value that came from Json")
    @Test
    public void deserializeTest() {
        var expectedJsonType = TypeFactory.createDefaultInstance().constructType(MockMapperEnum.class);
        BeanProperty beanProperty = mock(BeanProperty.class);
        when(beanProperty.getType()).thenReturn(expectedJsonType);

        var jsonDeserializer = (MapperEnumValueDeserializer) new MapperEnumValueDeserializer()
                .createContextual(mock(DeserializationContext.class), beanProperty);

        var jsonParser = Mockito.mock(JsonParser.class);
        when(jsonParser.getString()).thenReturn(MockMapperEnum.GENERIC_1.getValue());
        var context = Mockito.mock(DeserializationContext.class);

        var result = jsonDeserializer.deserialize(jsonParser, context);

        assertEquals(MockMapperEnum.GENERIC_1, result);
    }

    @DisplayName("Should throw JacksonException when failed to get string from JsonParser")
    @Test
    public void deserializeJacksonExceptionTest() {
        var expectedJsonType = TypeFactory.createDefaultInstance().constructType(MockMapperEnum.class);
        BeanProperty beanProperty = mock(BeanProperty.class);
        when(beanProperty.getType()).thenReturn(expectedJsonType);

        var jsonDeserializer = (MapperEnumValueDeserializer) new MapperEnumValueDeserializer()
                .createContextual(mock(DeserializationContext.class), beanProperty);

        var jsonParser = mock(JsonParser.class);
        when(jsonParser.getString()).thenThrow(JacksonException.class);
        var context = Mockito.mock(DeserializationContext.class);

        assertThrows(JacksonException.class, () -> jsonDeserializer.deserialize(jsonParser, context));
    }

    @DisplayName("Should return null value when currentToken is a VALUE_NULL")
    @Test
    public void deserializeVALUE_NULLTest() {
        var jsonType = TypeFactory.createDefaultInstance().constructType(MockMapperEnum.class);
        BeanProperty beanProperty = mock(BeanProperty.class);
        when(beanProperty.getType()).thenReturn(jsonType);

        var jsonDeserializer = (MapperEnumValueDeserializer) new MapperEnumValueDeserializer()
                .createContextual(mock(DeserializationContext.class), beanProperty);

        var jsonParser = Mockito.mock(JsonParser.class);
        when(jsonParser.currentToken()).thenReturn(JsonToken.VALUE_NULL);

        var context = Mockito.mock(DeserializationContext.class);

        var result = jsonDeserializer.deserialize(jsonParser, context);

        assertNull(result);
    }

    @DisplayName("Should return a new MapperEnumValueDeserializer instance with MockMapperEnum JsonType")
    @Test
    public void createContextualTest() {
        var expectedJsonType = TypeFactory.createDefaultInstance().constructType(MockMapperEnum.class);
        var contextMock = Mockito.mock(DeserializationContext.class);
        var propertyMock = Mockito.mock(BeanProperty.class);

        when(propertyMock.getType()).thenReturn(expectedJsonType);

        var original = new MapperEnumValueDeserializer();
        var jsonDeserializer = (MapperEnumValueDeserializer) original.createContextual(contextMock, propertyMock);

        assertNotSame(original, jsonDeserializer);
        assertEquals(expectedJsonType, jsonDeserializer.jsonType);
        assertNull(original.jsonType);
    }

}
