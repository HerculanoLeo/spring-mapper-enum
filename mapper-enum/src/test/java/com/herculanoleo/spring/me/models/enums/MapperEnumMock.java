package com.herculanoleo.spring.me.models.enums;

import com.herculanoleo.spring.me.models.annotation.MapperEnumType;
import com.herculanoleo.spring.me.models.enums.MapperEnum;

@MapperEnumType
public enum MapperEnumMock implements MapperEnum {

    ACTIVE("A"), INACTIVE("I"),
    ;

    private final String value;

    MapperEnumMock(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
