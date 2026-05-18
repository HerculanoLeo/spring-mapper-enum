package com.herculanoleo.spring.me.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.herculanoleo.spring.me.converter.json.MapperEnumJsonDeserializer;
import com.herculanoleo.spring.me.converter.json.MapperEnumJsonSerializer;
import com.herculanoleo.spring.me.converter.web.MapperEnumFormatterAnnotationFactory;
import com.herculanoleo.spring.me.converter.web.MapperEnumFormatterFactory;
import com.herculanoleo.spring.me.models.enums.MapperEnum;
import com.herculanoleo.spring.me.models.enums.MapperEnumMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.format.support.FormattingConversionService;

import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StartConfigurationTest {

    @Mock
    private FormattingConversionService conversionService;

    @Mock
    private MapperResourceLoader mapperResourceLoader;

    @Mock
    private ObjectMapper objectMapper;

    @Spy
    @InjectMocks
    private StartConfiguration startConfiguration;

    @DisplayName("Should config formatters and json serializables")
    @Test
    @SuppressWarnings("unchecked")
    public void setupTest() {
        try (var annotationFactoryMockedConstruction = Mockito.mockConstruction(MapperEnumFormatterAnnotationFactory.class);
             var simpleModuleMockedConstruction = Mockito.mockConstruction(SimpleModule.class, (mock, context) -> {
                 when(mock.addDeserializer(any(), any())).thenReturn(mock);
                 when(mock.addSerializer(any(), any())).thenReturn(mock);
             })) {
            Class<? extends MapperEnum> expectedClazz = MapperEnumMock.class;
            var factory = new MapperEnumFormatterFactory();
            var expectedFormatter = factory.getFormatter(MapperEnumMock.class);

            doReturn(Map.of(MapperEnumMock.class, expectedFormatter)).when(mapperResourceLoader).serializableEnumFormatter();

            doReturn(Set.of(MapperEnumMock.class)).when(mapperResourceLoader).getClasses();

            startConfiguration.setup();

            var simpleModule = simpleModuleMockedConstruction.constructed().stream().findFirst().orElseThrow();

            verify(conversionService).addFormatterForFieldType(eq(MapperEnumMock.class), eq(expectedFormatter));
            verify(conversionService).addFormatterForFieldAnnotation(
                    eq(annotationFactoryMockedConstruction.constructed().stream().findFirst().orElseThrow())
            );
            verify(simpleModule).addDeserializer(eq(MapperEnum.class), any(MapperEnumJsonDeserializer.class));
            verify(simpleModule).addDeserializer(eq((Class<MapperEnum>) expectedClazz), any(MapperEnumJsonDeserializer.class));
            verify(simpleModule).addSerializer(eq(MapperEnumMock.class), any(MapperEnumJsonSerializer.class));
            verify(objectMapper).registerModule(eq(simpleModule));
        }
    }

}
