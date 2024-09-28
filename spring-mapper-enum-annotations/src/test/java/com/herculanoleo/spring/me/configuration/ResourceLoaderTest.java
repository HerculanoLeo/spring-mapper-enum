package com.herculanoleo.spring.me.configuration;

import com.herculanoleo.spring.me.models.annotation.EnableMapperEnum;
import com.herculanoleo.spring.me.models.dto.WrongMapperMock;
import com.herculanoleo.spring.me.models.enums.MapperEnumMock;
import org.example.test.MockStarter;
import org.example.test.enums.OutsidePackageMapperMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceLoaderTest {

    @Mock
    private ApplicationContext applicationContext;

    @Spy
    @InjectMocks
    private ResourceLoader resourceLoader;

    @DisplayName("Should call findCandidateComponent and set classes when call setup")
    @Test
    public void setupTest() throws ClassNotFoundException {
        var expectResult = Set.of(MapperEnumMock.class);
        doReturn(expectResult).when(resourceLoader).findCandidateComponent();

        resourceLoader.setup();

        assertEquals(expectResult, resourceLoader.getClasses());
    }

    @DisplayName("Should create formatters for only enums that implement MapperEnum interfaces")
    @Test
    public void serializableEnumFormatterTest() {
        resourceLoader.classes = Set.of(MapperEnumMock.class, WrongMapperMock.class);
        var formattersMap = resourceLoader.serializableEnumFormatter();

        assertEquals(1, formattersMap.size());
        assertTrue(formattersMap.entrySet().stream().allMatch((entry) -> entry.getKey().isEnum()
                && Objects.nonNull(entry.getValue())));
    }

    @DisplayName("Should not create formatters for class that implement MapperEnum interfaces")
    @Test
    public void serializableEnumFormatterEmptyListTest() {
        resourceLoader.classes = Set.of(WrongMapperMock.class);
        var formattersMap = resourceLoader.serializableEnumFormatter();
        assertEquals(0, formattersMap.size());
    }

    @DisplayName("Should list all classes that implement MapperEnum")
    @Test
    public void findCandidateComponent() throws ClassNotFoundException {
        doReturn(
                Set.of(
                        "org.example.test",
                        "com.herculanoleo.spring",
                        "org.example.test.enums"
                )
        )
                .when(resourceLoader)
                .getBasePackages();
        var classes = this.resourceLoader.findCandidateComponent();
        assertTrue(classes.containsAll(List.of(
                WrongMapperMock.class,
                MapperEnumMock.class,
                OutsidePackageMapperMock.class
        )));
    }

    @DisplayName("Should list all base packages from the annotated class with EnableMapperEnum annotation")
    @Test
    public void getBasePackagesTest() {
        when(applicationContext.getBeansWithAnnotation(EnableMapperEnum.class))
                .thenReturn(Map.of(MockStarter.class.getCanonicalName(), new MockStarter()));

        when(applicationContext.findAnnotationOnBean(MockStarter.class.getCanonicalName(), EnableMapperEnum.class))
                .thenReturn(MockStarter.class.getAnnotation(EnableMapperEnum.class));

        var basePackages = resourceLoader.getBasePackages();
        assertTrue(basePackages.containsAll(List.of(
                "org.example.test",
                "com.herculanoleo.spring",
                "org.example.test.enums"
        )));
    }

    @DisplayName("Should return an empty list when not found an annotated class with EnableMapperEnum annotation")
    @Test
    public void getBasePackagesEmptyListTest() {
        when(applicationContext.getBeansWithAnnotation(EnableMapperEnum.class))
                .thenReturn(Map.of());

        var basePackages = resourceLoader.getBasePackages();
        assertTrue(basePackages.isEmpty());
    }

}
