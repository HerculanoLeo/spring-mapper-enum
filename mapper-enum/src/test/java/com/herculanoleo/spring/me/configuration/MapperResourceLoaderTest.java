package com.herculanoleo.spring.me.configuration;

import com.herculanoleo.spring.me.models.enums.MapperEnumMock;
import org.example.test.enums.OutsidePackageMapperMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MapperResourceLoaderTest {

    private final MapperResourceLoader mapperResourceLoader = new MapperResourceLoader();

    @DisplayName("Should load MapperEnum types from generated contributors at setup")
    @Test
    public void setupLoadsContributorsTest() {
        mapperResourceLoader.setup();

        var classes = mapperResourceLoader.getClasses();

        assertTrue(classes.contains(MapperEnumMock.class));
        assertTrue(classes.contains(OutsidePackageMapperMock.class));
        assertEquals(classes.size(), mapperResourceLoader.getContributors().size());
    }

    @DisplayName("Should create formatters for discovered enum types")
    @Test
    public void serializableEnumFormatterTest() {
        mapperResourceLoader.setup();

        var formattersMap = mapperResourceLoader.serializableEnumFormatter();

        assertFalse(formattersMap.isEmpty());
        assertTrue(formattersMap.entrySet().stream().allMatch((entry) -> entry.getKey().isEnum()
                && Objects.nonNull(entry.getValue())));
        assertNotNull(formattersMap.get(MapperEnumMock.class));
    }

}
