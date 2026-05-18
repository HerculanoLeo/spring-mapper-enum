package com.herculanoleo.spring.me.converter.web;

import com.herculanoleo.spring.me.models.annotation.MapperEnumFormat;
import com.herculanoleo.spring.me.models.enums.MapperEnum;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import java.util.Set;

@SuppressWarnings("unchecked")
public class MapperEnumFormatterAnnotationFactory implements AnnotationFormatterFactory<MapperEnumFormat> {

    private final Set<Class<?>> types;

    public MapperEnumFormatterAnnotationFactory(Set<Class<?>> types) {
        this.types = types;
    }

    @Override
    public Set<Class<?>> getFieldTypes() {
        return types;
    }

    @Override
    public Printer<?> getPrinter(MapperEnumFormat annotation, Class<?> fieldType) {
        if (MapperEnum.class.isAssignableFrom(fieldType)) {
            return new MapperEnumFormatter<>((Class<? extends MapperEnum>) fieldType);
        }
        return null;
    }

    @Override
    public Parser<?> getParser(MapperEnumFormat annotation, Class<?> fieldType) {
        if (MapperEnum.class.isAssignableFrom(fieldType)) {
            return new MapperEnumFormatter<>((Class<? extends MapperEnum>) fieldType);
        }
        return null;
    }
}
