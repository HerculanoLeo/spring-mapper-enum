package com.herculanoleo.spring.me.models.enums;

import com.herculanoleo.spring.me.models.annotation.MappedEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @MappedEnum(fallback = "GENERIC_1")
    private enum MockFallbackAnnotationEnum implements MapperEnum {
        GENERIC_1("G_1"),
        GENERIC_2("G_2"),
        ;
        private final String value;

        MockFallbackAnnotationEnum(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return this.value;
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

    @DisplayName("Should reject the Java constant name")
    @Test
    public void fromValueEnumsNameTest() {
        assertThrows(IllegalArgumentException.class, () -> MapperEnum.fromValue("GENERIC_1", MockSerializableEnum.class));
    }

    @DisplayName("Should return null when fromValue receives null")
    @Test
    public void fromValueNullTest() {
        assertNull(MapperEnum.fromValue(null, MockSerializableEnum.class));
    }

    @DisplayName("Should strip control characters from the reported invalid value")
    @Test
    public void fromValueSanitizesMessageTest() {
        var error = assertThrows(IllegalArgumentException.class, () -> MapperEnum.fromValue("bad\nvalue", MockSerializableEnum.class));
        assertTrue(error.getMessage().contains("badvalue"));
        assertTrue(!error.getMessage().contains("\n"));
    }

    @DisplayName("Should return generic enum of empty string value")
    @Test
    public void fromValueGenericEnumTest() {
        var enumMock = MapperEnum.fromValue("", MockSerializableWithGenericEnum.class);
        assertEquals(MockSerializableWithGenericEnum.GENERIC_1, enumMock);
    }

    @DisplayName("Should use the fallback constant declared on MappedEnum")
    @Test
    public void fromValueAnnotationFallbackTest() {
        var enumMock = MapperEnum.fromValue("unknown", MockFallbackAnnotationEnum.class);
        assertEquals(MockFallbackAnnotationEnum.GENERIC_1, enumMock);
    }

    @DisplayName("Should throw IllegalArgumentException when generic is null")
    @Test
    public void fromValueInvalidEnumExceptionWithNullGenericEnumTest() {
        assertThrows(IllegalArgumentException.class, () -> MapperEnum.fromValue("", MockSerializableEnum.class));
    }

    @DisplayName("Should throw IllegalArgumentException when generic is not a Enum")
    @Test
    public void genericInvalidEnumExceptionWithNonEnumGenericEnumTest() {
        assertThrows(IllegalArgumentException.class, () -> MapperEnum.generic(MockMapperClass.class));
    }

    @DisplayName("Should return empty Optional when findByValue receives null")
    @Test
    public void findByValueWithNullValueTest() {
        assertTrue(MapperEnum.findByValue(null, MockSerializableEnum.class).isEmpty());
    }

    @DisplayName("Should return Optional with enum when findByValue receives a valid value")
    @Test
    public void findByValueWithValidValueTest() {
        var enumMock = MapperEnum.findByValue("G_1", MockSerializableEnum.class);
        assertEquals(MockSerializableEnum.GENERIC_1, enumMock.orElseThrow());
    }

    @DisplayName("Should return empty Optional when findByValue receives the Java constant name")
    @Test
    public void findByValueIgnoresEnumNameTest() {
        assertTrue(MapperEnum.findByValue("GENERIC_1", MockSerializableEnum.class).isEmpty());
    }

    @DisplayName("Should return empty Optional when findByValue receives empty string")
    @Test
    public void findByValueWithEmptyValueTest() {
        assertTrue(MapperEnum.findByValue("", MockSerializableEnum.class).isEmpty());
    }

    @DisplayName("Should return empty Optional when findByValue receives unknown value even if generic exists")
    @Test
    public void findByValueDoesNotUseGenericTest() {
        assertTrue(MapperEnum.findByValue("", MockSerializableWithGenericEnum.class).isEmpty());
    }

}
