package com.herculanoleo.spring.me.converter.web;

import com.herculanoleo.spring.me.models.enums.MapperEnum;
import org.springframework.format.Formatter;

import java.util.Locale;

public class MapperEnumFormatter<E extends MapperEnum> implements Formatter<E> {

    protected final Class<E> targetType;

    public MapperEnumFormatter(Class<E> targetType) {
        this.targetType = targetType;
    }

    @Override
    public E parse(String text, Locale locale) {
        return MapperEnum.fromValue(text, targetType);
    }

    @Override
    public String print(E object, Locale locale) {
        return object.getValue();
    }
}
