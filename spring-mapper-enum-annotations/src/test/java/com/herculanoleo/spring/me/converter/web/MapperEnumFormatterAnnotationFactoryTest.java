package com.herculanoleo.spring.me.converter.web;

import com.herculanoleo.spring.me.models.annotation.MapperEnumFormat;
import com.herculanoleo.spring.me.models.enums.MapperEnumMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MapperEnumFormatterAnnotationFactoryTest {

    public static final class MockClass {
    }

    private final MapperEnumFormatterAnnotationFactory annotationFactory = new MapperEnumFormatterAnnotationFactory(
            Set.of(MapperEnumMock.class)
    );

    @DisplayName("Should return a list of MapperEnum types")
    @Test
    public void getFieldTypesTest() {
        var types = annotationFactory.getFieldTypes();
        assertTrue(types.contains(MapperEnumMock.class));
    }

    @DisplayName("Should return a MapperEnumFormatter when pass a MapperEnum Enum as argument of getPrinter")
    @Test
    public void getPrinterTest() {
        Printer<?> printer = annotationFactory.getPrinter(Mockito.mock(MapperEnumFormat.class), MapperEnumMock.class);
        assertNotNull(printer);
    }

    @DisplayName("Should return a null when pass a class that does not implement MapperEnum")
    @Test
    public void getPrinterWrongClassTest() {
        Printer<?> printer = annotationFactory.getPrinter(Mockito.mock(MapperEnumFormat.class), MockClass.class);
        assertNull(printer);
    }

    @DisplayName("Should return a MapperEnumFormatter when pass a MapperEnum Enum as argument of getPrinter")
    @Test
    public void getParserTest() {
        Parser<?> parser = annotationFactory.getParser(Mockito.mock(MapperEnumFormat.class), MapperEnumMock.class);
        assertNotNull(parser);
    }

    @DisplayName("Should return a null when pass a class that does not implement MapperEnum")
    @Test
    public void getParserWrongClassTest() {
        Parser<?> parser = annotationFactory.getParser(Mockito.mock(MapperEnumFormat.class), MockClass.class);
        assertNull(parser);
    }

}
