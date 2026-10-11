package com.herculanoleo.spring.me.models.feign;

import com.herculanoleo.spring.me.models.enums.MapperEnum;
import feign.Param;
import feign.QueryMapEncoder;
import feign.codec.EncodeException;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Feign {@link QueryMapEncoder} that encodes {@link MapperEnum} values with
 * {@link MapperEnum#getValue()} using record accessors or JavaBean getters.
 *
 * <p>Static members and {@code transient} properties are omitted. Private fields are read only
 * through their accessor.
 */
public class MapperEnumQueryMapEncoder implements QueryMapEncoder {

    @Override
    public Map<String, Object> encode(Object object) throws EncodeException {
        if (object == null) {
            return Collections.emptyMap();
        }
        try {
            var encoded = new LinkedHashMap<String, Object>();
            if (object.getClass().isRecord()) {
                encodeRecord(object, encoded);
            } else {
                encodeBean(object, encoded);
            }
            return encoded;
        } catch (ReflectiveOperationException | IntrospectionException ex) {
            throw new EncodeException("Failure encoding object into query map", ex);
        }
    }

    private void encodeRecord(Object object, Map<String, Object> encoded) throws IllegalAccessException, InvocationTargetException {
        for (RecordComponent component : object.getClass().getRecordComponents()) {
            if (Modifier.isStatic(component.getAccessor().getModifiers()) || isTransient(object.getClass(), component.getName())) {
                continue;
            }
            var raw = component.getAccessor().invoke(object);
            var alias = component.getAnnotation(Param.class);
            if (alias == null) {
                alias = component.getAccessor().getAnnotation(Param.class);
            }
            put(encoded, alias != null ? alias.value() : component.getName(), raw);
        }
    }

    private void encodeBean(Object object, Map<String, Object> encoded) throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        BeanInfo info = Introspector.getBeanInfo(object.getClass(), Object.class);
        for (PropertyDescriptor descriptor : info.getPropertyDescriptors()) {
            Method reader = descriptor.getReadMethod();
            if (reader == null || Modifier.isStatic(reader.getModifiers()) || isTransient(object.getClass(), descriptor.getName())) {
                continue;
            }
            var raw = reader.invoke(object);
            var alias = reader.getAnnotation(Param.class);
            put(encoded, alias != null ? alias.value() : descriptor.getName(), raw);
        }
    }

    private void put(Map<String, Object> encoded, String name, Object raw) {
        if (raw == null) {
            return;
        }
        encoded.put(name, raw instanceof MapperEnum mapperEnum ? mapperEnum.getValue() : raw);
    }

    private boolean isTransient(Class<?> type, String name) {
        for (Class<?> current = type; current != null && current != Object.class; current = current.getSuperclass()) {
            try {
                Field field = current.getDeclaredField(name);
                return Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers());
            } catch (NoSuchFieldException ignored) {
                // The property may be declared on a supertype.
            }
        }
        return false;
    }

}
