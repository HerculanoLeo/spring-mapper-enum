package com.herculanoleo.spring.me.converter.web;

import com.herculanoleo.spring.me.models.enums.MapperEnum;
import org.springframework.format.Formatter;
import org.springframework.lang.NonNull;

/**
 * Factory for {@link MapperEnumFormatter} instances used by {@link com.herculanoleo.spring.me.configuration.MapperResourceLoader}.
 */
public class MapperEnumFormatterFactory {

    /**
     * Returns a formatter bound to the given enum type.
     *
     * @param targetType the enum class
     * @return a new formatter instance
     */
    public Formatter<? extends MapperEnum> getFormatter(@NonNull final Class<? extends MapperEnum> targetType) {
        return new MapperEnumFormatter<>(targetType);
    }
}
