package com.herculanoleo.spring.me.converter.web;

import com.herculanoleo.spring.me.models.enums.MapperEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MapperEnumFormatterTest {

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
    }

    private final MapperEnumFormatter<MockMapperEnum> formatter = new MapperEnumFormatter<>(MockMapperEnum.class);

    @DisplayName("Should return the Enum of string value")
    @Test
    public void parseTest() {
        var locale = Mockito.mock(Locale.class);
        var result = formatter.parse(MockMapperEnum.GENERIC_1.getValue(), locale);
        assertEquals(MockMapperEnum.GENERIC_1, result);
    }

    @DisplayName("Should return the string value of the Enum")
    @Test
    public void printTest() {
        var locale = Mockito.mock(Locale.class);
        var result = formatter.print(MockMapperEnum.GENERIC_1, locale);
        assertEquals(MockMapperEnum.GENERIC_1.getValue(), result);
    }

}
