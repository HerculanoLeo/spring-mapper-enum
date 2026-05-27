package com.herculanoleo.spring.me.converter.web;

import com.herculanoleo.spring.me.models.enums.MapperEnum;
import org.springframework.format.Formatter;

import java.util.Locale;

/**
 * Spring {@link Formatter} that converts between request/path/query strings and {@link MapperEnum} constants.
 *
 * <p>{@link #parse(String, Locale)} delegates to {@link MapperEnum#fromValue(String, Class)}.
 * {@link #print(MapperEnum, Locale)} returns {@link MapperEnum#getValue()}.
 *
 * <p>Registered automatically for each {@link com.herculanoleo.spring.me.models.annotation.MapperEnumType}
 * enum when {@link com.herculanoleo.spring.me.models.annotation.EnableMapperEnum} is active.
 */
public class MapperEnumFormatter<E extends MapperEnum> implements Formatter<E> {

    protected final Class<E> targetType;

    /**
     * Creates a formatter for the given enum type.
     *
     * @param targetType the enum class to format
     */
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
