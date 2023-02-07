package com.oneandone.ejbcdiunit5.cdibeans;

import java.lang.reflect.Field;

/**
 * @author aschoerk
 */
public class ReflectUtils {
    private ReflectUtils() {}

    public static void setField(Object object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set " + fieldName + " of object", e);
        }
    }

    public static void setField(Object object, Field fld, Object value) {
        try {
            fld.setAccessible(true);
            fld.set(object, value);
        } catch (IllegalAccessException e) {
            String fieldName = null == fld ? "n/a" : fld.getName();
            throw new RuntimeException("Failed to set " + fieldName + " of object", e);
        }
    }

}
