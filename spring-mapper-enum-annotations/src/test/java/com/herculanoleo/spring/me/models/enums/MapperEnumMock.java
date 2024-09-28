package com.herculanoleo.spring.me.models.enums;

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
