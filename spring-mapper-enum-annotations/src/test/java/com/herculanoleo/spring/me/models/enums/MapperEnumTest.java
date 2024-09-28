package com.herculanoleo.spring.me.models.enums;

import com.herculanoleo.spring.me.models.exception.InvalidGenericEnumException;
import com.herculanoleo.spring.me.models.exception.InvalidValueEnumException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class MapperEnumTest {

    private enum MockSerializableEnum implements MapperEnum {
        GENERIC_1("G_1"),
        GENERIC_2("G_2"),
        ;
        private final String value;

        MockSerializableEnum(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return this.value;
        }
    }

    private enum MockSerializableWithGenericEnum implements MapperEnum {
        GENERIC_1("G_1"),
        ;
        private final String value;

        MockSerializableWithGenericEnum(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return this.value;
        }

        @Override
        public MapperEnum getGeneric() {
            return GENERIC_1;
        }
    }

    private static class MockMapperClass implements MapperEnum {

        private final String value;

        private MockMapperClass(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return this.value;
        }

        @Override
        public MapperEnum getGeneric() {
            return new MockMapperClass("Test");
        }
    }

    @DisplayName("Should return enum of string value")
    @Test
    public void fromValueTest() {
        var enumMock = MapperEnum.fromValue("G_1", MockSerializableEnum.class);
        assertEquals(MockSerializableEnum.GENERIC_1, enumMock);
    }

    @DisplayName("Should return enum of enum's name value")
    @Test
    public void fromValueEnumsNameTest() {
        var enumMock = MapperEnum.fromValue("GENERIC_1", MockSerializableEnum.class);
        assertEquals(MockSerializableEnum.GENERIC_1, enumMock);
    }

    @DisplayName("Should return generic enum of empty string value")
    @Test
    public void fromValueGenericEnumTest() {
        var enumMock = MapperEnum.fromValue("", MockSerializableWithGenericEnum.class);
        assertEquals(MockSerializableWithGenericEnum.GENERIC_1, enumMock);
    }

    @DisplayName("Should throw InvalidEnumException when generic is null")
    @Test
    public void fromValueInvalidEnumExceptionWithNullGenericEnumTest() {
        assertThrows(InvalidValueEnumException.class, () -> MapperEnum.fromValue("", MockSerializableEnum.class));
    }

    @DisplayName("Should throw InvalidEnumException when generic is not a Enum")
    @Test
    public void genericInvalidEnumExceptionWithNonEnumGenericEnumTest() {
        assertThrows(InvalidGenericEnumException.class, () -> MapperEnum.generic(MockMapperClass.class));
    }

}
