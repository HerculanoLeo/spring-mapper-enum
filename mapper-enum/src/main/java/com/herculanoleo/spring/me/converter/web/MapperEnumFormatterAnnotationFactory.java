package com.herculanoleo.spring.me.converter.web;

import com.herculanoleo.spring.me.models.annotation.MapperEnumFormat;
import com.herculanoleo.spring.me.models.enums.MapperEnum;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link AnnotationFormatterFactory} for {@link MapperEnumFormat}.
 *
 * <p>Provides {@link MapperEnumFormatter} instances for fields and parameters annotated with
 * {@link MapperEnumFormat}, limited to the set of compile-time registered enum types.
 */
@SuppressWarnings("unchecked")
public class MapperEnumFormatterAnnotationFactory implements AnnotationFormatterFactory<MapperEnumFormat> {

    private final Set<Class<?>> types;

    private final ConcurrentHashMap<Class<? extends MapperEnum>, MapperEnumFormatter<?>> formatters = new ConcurrentHashMap<>();

    /**
     * @param types compile-time registered enum types (from {@link com.herculanoleo.spring.me.configuration.MapperResourceLoader#getClasses()})
     */
    public MapperEnumFormatterAnnotationFactory(Set<Class<?>> types) {
        this.types = types;
    }

    @Override
    public Set<Class<?>> getFieldTypes() {
        return types;
    }

    @Override
    public Printer<?> getPrinter(MapperEnumFormat annotation, Class<?> fieldType) {
        return formatterFor(fieldType);
    }

    @Override
    public Parser<?> getParser(MapperEnumFormat annotation, Class<?> fieldType) {
        return formatterFor(fieldType);
    }

    private MapperEnumFormatter<?> formatterFor(Class<?> fieldType) {
        if (!MapperEnum.class.isAssignableFrom(fieldType) || !fieldType.isEnum()) {
            return null;
        }
        var enumType = (Class<? extends MapperEnum>) fieldType;
        return formatters.computeIfAbsent(enumType, MapperEnumFormatter::new);
    }
}
