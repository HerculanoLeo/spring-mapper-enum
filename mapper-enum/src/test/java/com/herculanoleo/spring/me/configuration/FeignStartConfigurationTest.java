package com.herculanoleo.spring.me.configuration;

import com.herculanoleo.spring.me.converter.web.MapperEnumFormatterAnnotationFactory;
import com.herculanoleo.spring.me.converter.web.MapperEnumFormatterFactory;
import com.herculanoleo.spring.me.models.enums.MapperEnumMock;
import com.herculanoleo.spring.me.models.feign.MapperEnumQueryMapEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.format.FormatterRegistry;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FeignStartConfigurationTest {

    @Mock
    private MapperResourceLoader mapperResourceLoader;

    @Spy
    @InjectMocks
    private FeignStartConfiguration feignStartConfiguration;

    @DisplayName("Should config formatters")
    @Test
    public void registerFormattersTest() {
        try (var annotationFactoryMockedConstruction = Mockito.mockConstruction(MapperEnumFormatterAnnotationFactory.class)) {
            var expectedClazz = MapperEnumMock.class;
            var factory = new MapperEnumFormatterFactory();
            var expectedFormatter = factory.getFormatter(MapperEnumMock.class);

            doReturn(Map.of(MapperEnumMock.class, expectedFormatter))
                    .when(mapperResourceLoader).serializableEnumFormatter();
            doReturn(Set.of(MapperEnumMock.class)).when(mapperResourceLoader).getClasses();

            var registry = mock(FormatterRegistry.class);

            feignStartConfiguration.registerFormatters(registry);

            verify(registry).addFormatterForFieldType(eq(expectedClazz), eq(expectedFormatter));
            verify(registry).addFormatterForFieldAnnotation(
                    eq(annotationFactoryMockedConstruction.constructed().stream().findFirst().orElseThrow())
            );
        }
    }

    @DisplayName("Should return a QueryMapEncoder")
    @Test
    public void serializableEnumQueryMapEncoderTest() {
        var queryMapEncoder = this.feignStartConfiguration.serializableEnumQueryMapEncoder();
        assertNotNull(queryMapEncoder);
        assertInstanceOf(MapperEnumQueryMapEncoder.class, queryMapEncoder);
    }

}
