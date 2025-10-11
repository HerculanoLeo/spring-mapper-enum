package org.example.test.enums;

import com.herculanoleo.spring.me.models.enums.MapperEnum;

public enum OutsidePackageMapperMock implements MapperEnum {
    ACTIVE("A"), INACTIVE("I"),
    ;
    private final String value;

    OutsidePackageMapperMock(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
