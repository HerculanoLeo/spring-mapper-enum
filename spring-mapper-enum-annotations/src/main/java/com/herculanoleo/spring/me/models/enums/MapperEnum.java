package com.herculanoleo.spring.me.models.enums;


import com.herculanoleo.spring.me.models.exception.InvalidGenericEnumException;
import com.herculanoleo.spring.me.models.exception.InvalidValueEnumException;

import java.util.Arrays;

public interface MapperEnum {

    String getValue();

    default MapperEnum getGeneric() {
        return null;
    }

    default String getErrorMessage() {
        return null;
    }

    static <E extends MapperEnum> E generic(Class<E> clazz) {
        if (clazz.isEnum()) {
            var generic = Arrays.stream(clazz.getEnumConstants())
                    .findFirst()
                    .map(MapperEnum::getGeneric)
                    .orElse(null);

            return clazz.cast(generic);
        }
        throw new InvalidGenericEnumException();
    }

    static <E extends MapperEnum> E fromValue(String value, Class<E> clazz) {
        var opEnum = Arrays.stream(clazz.getEnumConstants())
                .filter(e -> value.equals(e.getValue()) || value.equals(((Enum<?>) e).name()))
                .findFirst();

        var generic = MapperEnum.generic(clazz);

        if (null == generic && opEnum.isEmpty()) {
            var op = Arrays.stream(clazz.getEnumConstants()).findFirst();
            String message = op.map(MapperEnum::getErrorMessage).orElse("invalid enum value");
            throw new InvalidValueEnumException("%s - %s".formatted(message, value));
        }

        return opEnum.orElse(clazz.cast(generic));
    }

}
