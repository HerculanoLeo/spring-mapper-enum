package com.herculanoleo.spring.me.converter.json;

import com.herculanoleo.spring.me.models.enums.MapperEnum;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.exc.InvalidFormatException;

/**
 * Shared Jackson read path used by generated per-type deserializers.
 *
 * <p>A null token or a blank string becomes {@code null} (an empty filter). Any other token
 * becomes {@link InvalidFormatException} so Spring maps the body to HTTP 400.
 */
public final class MapperEnumJson {

    private MapperEnumJson() {
    }

    /**
     * Reads one {@link MapperEnum} value from the current parser token.
     *
     * @param parser  the JSON parser positioned on the value
     * @param context the current deserialization context
     * @param type    the target enum class
     * @param <E>     the enum type
     * @return the matching constant, or {@code null} when the token is null or blank
     * @throws InvalidFormatException when the token is not a string or the text is not a mapped value
     */
    public static <E extends MapperEnum> E read(JsonParser parser, DeserializationContext context, Class<E> type) {
        var token = parser.currentToken();
        if (token == JsonToken.VALUE_NULL) {
            return null;
        }
        if (token != JsonToken.VALUE_STRING) {
            throw InvalidFormatException.from(
                    parser,
                    "MapperEnum value must be a string",
                    parser.getValueAsString(),
                    type
            );
        }

        var text = parser.getString();
        if (text == null || text.isBlank()) {
            return null;
        }

        try {
            return MapperEnum.fromValue(text, type);
        } catch (IllegalArgumentException ex) {
            var failure = InvalidFormatException.from(parser, ex.getMessage(), text, type);
            failure.initCause(ex);
            throw failure;
        }
    }

}
