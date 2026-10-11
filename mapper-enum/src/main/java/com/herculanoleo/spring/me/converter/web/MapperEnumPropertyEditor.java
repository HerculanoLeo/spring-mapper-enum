package com.herculanoleo.spring.me.converter.web;

import com.herculanoleo.spring.me.models.enums.MapperEnum;

import java.beans.PropertyEditorSupport;

/**
 * Binds query and path strings with {@link MapperEnum#fromValue(String, Class)}.
 *
 * <p>Registered as a custom editor so servlet and reactive data binding do not fall back to
 * {@link Enum#valueOf(Class, String)} after a formatter rejects the constant name.
 */
public class MapperEnumPropertyEditor extends PropertyEditorSupport {

    private final Class<? extends MapperEnum> targetType;

    public MapperEnumPropertyEditor(Class<? extends MapperEnum> targetType) {
        this.targetType = targetType;
    }

    @Override
    public void setAsText(String text) {
        if (text == null || text.isBlank()) {
            setValue(null);
            return;
        }
        setValue(MapperEnum.fromValue(text, targetType));
    }

    @Override
    public String getAsText() {
        var value = getValue();
        return value instanceof MapperEnum mapperEnum ? mapperEnum.getValue() : "";
    }

}
