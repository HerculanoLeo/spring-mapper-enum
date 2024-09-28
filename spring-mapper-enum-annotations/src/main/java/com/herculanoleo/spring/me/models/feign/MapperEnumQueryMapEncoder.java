package com.herculanoleo.spring.me.models.feign;

import com.herculanoleo.spring.me.models.enums.MapperEnum;
import feign.Param;
import feign.QueryMapEncoder;
import feign.codec.EncodeException;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/*
   This class is a simple copy of the FieldQueryMapEncoder class from Feign library with the addition that if the field is a SerializableEnum
   value, call SerializableEnum.getValue() method instead of the default Object.toString()
 */
public class MapperEnumQueryMapEncoder implements QueryMapEncoder {

    private final Map<Class<?>, ObjectParamMetadata> classToMetadata =
            new ConcurrentHashMap<>();

    @Override
    public Map<String, Object> encode(Object object) throws EncodeException {
        if (object == null) {
            return Collections.emptyMap();
        }
        ObjectParamMetadata metadata =
                classToMetadata.computeIfAbsent(object.getClass(), ObjectParamMetadata::parseObjectType);

        return metadata.objectFields.stream()
                .map(field -> this.FieldValuePair(object, field))
                .filter(fieldObjectPair -> fieldObjectPair.right.isPresent())
                .collect(Collectors.toMap(this::fieldName,
                        fieldObjectPair -> fieldObjectPair.right.get()));
    }

    private String fieldName(Pair<Field, Optional<Object>> pair) {
        Param alias = pair.left.getAnnotation(Param.class);
        return alias != null ? alias.value() : pair.left.getName();
    }

    private Pair<Field, Optional<Object>> FieldValuePair(Object object, Field field) {
        try {
            var optional = Optional.ofNullable(field.get(object))
                    .map(value -> {
                        if (value instanceof MapperEnum e) {
                            return e.getValue();
                        }
                        return value;
                    });

            return Pair.pair(field, optional);
        } catch (IllegalAccessException e) {
            throw new EncodeException("Failure encoding object into query map", e);
        }
    }

    private record ObjectParamMetadata(List<Field> objectFields) {

        private ObjectParamMetadata(List<Field> objectFields) {
            this.objectFields = Collections.unmodifiableList(objectFields);
        }

        private static ObjectParamMetadata parseObjectType(Class<?> type) {
            List<Field> allFields = new ArrayList<>();

            for (Class<?> currentClass = type; currentClass != null; currentClass =
                    currentClass.getSuperclass()) {
                Collections.addAll(allFields, currentClass.getDeclaredFields());
            }

            return new ObjectParamMetadata(allFields.stream()
                    .filter(field -> !field.isSynthetic())
                    .peek(field -> field.setAccessible(true))
                    .collect(Collectors.toList()));
        }
    }

    private record Pair<T, U>(T left, U right) {
        public static <T, U> Pair<T, U> pair(T left, U right) {
            return new Pair<>(left, right);
        }
    }
}
