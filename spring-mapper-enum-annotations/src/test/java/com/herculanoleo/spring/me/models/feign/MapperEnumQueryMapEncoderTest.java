package com.herculanoleo.spring.me.models.feign;

import com.herculanoleo.spring.me.models.dto.MapperEnumQueryMapEncoderObject;
import com.herculanoleo.spring.me.models.enums.MapperEnumMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MapperEnumQueryMapEncoderTest {

    private final MapperEnumQueryMapEncoder queryMapEncoder = new MapperEnumQueryMapEncoder();

    @DisplayName("Should return map of encoded values of object")
    @Test
    public void encodeTest() {
        var object = new MapperEnumQueryMapEncoderObject("test1", MapperEnumMock.ACTIVE, MapperEnumMock.INACTIVE);
        var expectedMap = Map.of(
                "value1", "test1",
                "value2", MapperEnumMock.ACTIVE.getValue(),
                "param3", MapperEnumMock.INACTIVE.getValue()

        );

        var result = queryMapEncoder.encode(object);
        assertEquals(expectedMap, result);
    }

    @DisplayName("Should return empty map when pass null value as an argument")
    @Test
    public void encodeNullTest() {
        var result = queryMapEncoder.encode(null);
        assertTrue(result.isEmpty());
    }

}
