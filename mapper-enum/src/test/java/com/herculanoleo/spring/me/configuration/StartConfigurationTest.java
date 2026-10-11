package com.herculanoleo.spring.me.configuration;

import com.herculanoleo.spring.me.converter.json.MapperEnumValueDeserializer;
import com.herculanoleo.spring.me.converter.web.MapperEnumFormatterAnnotationFactory;
import com.herculanoleo.spring.me.converter.web.MapperEnumFormatterFactory;
import com.herculanoleo.spring.me.models.enums.MapperEnum;
import com.herculanoleo.spring.me.models.enums.MapperEnumMock;
import com.herculanoleo.spring.me.spi.MapperEnumTypeContributor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.format.FormatterRegistry;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.module.SimpleModule;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StartConfigurationTest {

    @Mock
    private MapperResourceLoader mapperResourceLoader;

    @Mock
    private MapperEnumTypeContributor contributor;

    @InjectMocks
    private StartConfiguration startConfiguration;

    @DisplayName("Should config formatters")
    @Test
    public void setupTest() {
        try (var annotationFactoryMockedConstruction = Mockito.mockConstruction(MapperEnumFormatterAnnotationFactory.class)) {
            var factory = new MapperEnumFormatterFactory();
            var expectedFormatter = factory.getFormatter(MapperEnumMock.class);
            var registry = mock(FormatterRegistry.class);
            var webMvc = new MapperEnumWebMvcConfiguration(mapperResourceLoader);

            doReturn(Map.of(MapperEnumMock.class, expectedFormatter)).when(mapperResourceLoader).serializableEnumFormatter();
            doReturn(Set.of(MapperEnumMock.class)).when(mapperResourceLoader).getClasses();

            webMvc.addFormatters(registry);

            verify(registry).addFormatterForFieldType(eq(MapperEnumMock.class), eq(expectedFormatter));
            verify(registry).addFormatterForFieldAnnotation(
                    eq(annotationFactoryMockedConstruction.constructed().stream().findFirst().orElseThrow())
            );
        }
    }

    @DisplayName("Should config json serializables module from generated contributors")
    @Test
    @SuppressWarnings("unchecked")
    public void mapperEnumModuleTest() {
        when(contributor.enumType()).thenReturn((Class) MapperEnumMock.class);
        when(contributor.serializer()).thenReturn(mock(ValueSerializer.class));
        when(contributor.deserializer()).thenReturn(mock(ValueDeserializer.class));

        try (var simpleModuleMockedConstruction = Mockito.mockConstruction(SimpleModule.class, (mock, context) -> {
            when(mock.addDeserializer(any(), any())).thenReturn(mock);
            when(mock.addSerializer(any(), any())).thenReturn(mock);
        })) {
            doReturn(List.of(contributor)).when(mapperResourceLoader).getContributors();

            var module = startConfiguration.mapperEnumModule();

            var simpleModule = simpleModuleMockedConstruction.constructed().stream().findFirst().orElseThrow();

            verify(simpleModule).addDeserializer(eq(MapperEnum.class), any(MapperEnumValueDeserializer.class));
            verify(simpleModule, atLeastOnce()).addDeserializer(any(), any());
            verify(simpleModule, atLeastOnce()).addSerializer(any(), any());
            assertSame(simpleModule, module);
        }
    }

}
