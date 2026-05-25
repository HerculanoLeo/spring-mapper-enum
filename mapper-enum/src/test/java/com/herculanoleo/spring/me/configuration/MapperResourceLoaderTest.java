package com.herculanoleo.spring.me.configuration;

import com.herculanoleo.spring.me.models.annotation.EnableMapperEnum;
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
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MapperResourceLoaderTest {

    @Mock
    private ApplicationContext applicationContext;

    @Spy
    @InjectMocks
    private MapperResourceLoader mapperResourceLoader;

    @DisplayName("Should call findCandidateComponent and set classes when call setup")
    @Test
    public void setupTest() throws ClassNotFoundException {
        var expectResult = List.of(MapperEnumMock.class);
        doReturn(Set.of("com.herculanoleo.spring.me")).when(mapperResourceLoader).getBasePackages();
        doReturn(expectResult).when(mapperResourceLoader).findCandidateComponent(anyCollection());

        mapperResourceLoader.setup();

        assertEquals(expectResult, mapperResourceLoader.getClasses());
        assertEquals(1, mapperResourceLoader.serializableEnumFormatter().size());
    }

    @DisplayName("Should create formatters only for enum types discovered during setup")
    @Test
    public void serializableEnumFormatterTest() throws ClassNotFoundException {
        doReturn(Set.of("com.herculanoleo.spring.me")).when(mapperResourceLoader).getBasePackages();
        doReturn(List.of(MapperEnumMock.class)).when(mapperResourceLoader).findCandidateComponent(anyCollection());

        mapperResourceLoader.setup();

        var formattersMap = mapperResourceLoader.serializableEnumFormatter();

        assertEquals(1, formattersMap.size());
        assertTrue(formattersMap.entrySet().stream().allMatch((entry) -> entry.getKey().isEnum()
                && Objects.nonNull(entry.getValue())));
    }

    @DisplayName("Should not create formatters when no enums were discovered")
    @Test
    public void serializableEnumFormatterEmptyListTest() throws ClassNotFoundException {
        doReturn(Set.of("com.herculanoleo.spring.me")).when(mapperResourceLoader).getBasePackages();
        doReturn(List.of()).when(mapperResourceLoader).findCandidateComponent(anyCollection());

        mapperResourceLoader.setup();

        assertEquals(0, mapperResourceLoader.serializableEnumFormatter().size());
    }

    @DisplayName("Should list only enum classes that implement MapperEnum")
    @Test
    public void findCandidateComponent() throws ClassNotFoundException {
        when(applicationContext.getClassLoader()).thenReturn(getClass().getClassLoader());
        var basePackages = Set.of(
                "org.example.test",
                "com.herculanoleo.spring.me",
                "org.example.test.enums"
        );
        var classes = this.mapperResourceLoader.findCandidateComponent(basePackages);
        assertTrue(classes.contains(MapperEnumMock.class));
        assertTrue(classes.contains(OutsidePackageMapperMock.class));
        assertTrue(classes.stream().allMatch(Class::isEnum));
    }

    @DisplayName("Should list all base packages from the annotated class with EnableMapperEnum annotation")
    @Test
    public void getBasePackagesTest() {
        when(applicationContext.getBeansWithAnnotation(EnableMapperEnum.class))
                .thenReturn(Map.of(MockStarter.class.getCanonicalName(), new MockStarter()));

        when(applicationContext.findAnnotationOnBean(MockStarter.class.getCanonicalName(), EnableMapperEnum.class))
                .thenReturn(MockStarter.class.getAnnotation(EnableMapperEnum.class));

        var basePackages = mapperResourceLoader.getBasePackages();
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

        var basePackages = mapperResourceLoader.getBasePackages();
        assertTrue(basePackages.isEmpty());
    }

}
