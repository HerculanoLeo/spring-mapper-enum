package com.herculanoleo.spring.me.converter.web;

import com.herculanoleo.spring.me.models.enums.MapperEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class MapperEnumFormatterFactoryTest {

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

    @DisplayName("Should create a instance of MockMapperEnum Formatter")
    @Test
    public void getFormatterTest() {
        var formatterFactory = new MapperEnumFormatterFactory();

        var formatter = formatterFactory.getFormatter(MockMapperEnum.class);

        assertInstanceOf(MapperEnumFormatter.class, formatter);
    }
}
