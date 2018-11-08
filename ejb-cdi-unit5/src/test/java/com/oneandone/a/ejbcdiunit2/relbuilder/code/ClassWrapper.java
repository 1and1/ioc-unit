package com.oneandone.a.ejbcdiunit2.relbuilder.code;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wraps Class-Object to handle RuntimeErrors when classes are not available
 *
 * @author aschoerk
 */
public class ClassWrapper {
    static Logger logger = LoggerFactory.getLogger(ClassWrapper.class);
    private final Type type;
    private Boolean available = null;

    private Class getClazz() {
        if (type instanceof Class)
            return (Class) type;
        else if (type instanceof ParameterizedType) {
            return (Class) (((ParameterizedType) type).getRawType());
        } else
            return null;
    }

    public Type getType() {
        return type;
    }

    public ClassWrapper(Type type) {
        this.type = type;
    }

    public ClassWrapper(ParameterizedType type) {
        this.type = type;
    }

    public boolean isGeneric() {
        return type instanceof ParameterizedType;
    }


    public ClassWrapper(final Field field) {
        this.type = field.getType();
    }

    public ClassWrapper(final Constructor constructor) {
        this.type = constructor.getDeclaringClass();
    }

    public ClassWrapper(final Parameter parameter) {
        this.type = parameter.getType();
    }

    public ClassWrapper(final Method method) {
        this.type = method.getReturnType();
    }

    public boolean isInterface() {
        return getClazz().isInterface();
    }

    public ClassWrapper getGenericSuperclass() {
        return new ClassWrapper(getClazz().getGenericSuperclass());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ClassWrapper that = (ClassWrapper) o;
        return Objects.equals(type, that.type);
    }

    public boolean equals(final Class<?> c) {
        return Objects.equals(type, c);
    }


    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    public Field[] getDeclaredFields() {
        try {
            return getClazz().getDeclaredFields();
        } catch (NoClassDefFoundError e) {
            logger.warn("Class not available during getDeclaredFields", e);
            return new Field[0];
        }
    }

    public Constructor<?>[] getDeclaredConstructors() {
        try {
            return getClazz().getDeclaredConstructors();
        } catch (NoClassDefFoundError e) {
            logger.warn("Class not available during getDeclaredConstructors", e);
            return new Constructor[0];
        }
    }

    public Method[] getDeclaredMethods() {
        try {
            return getClazz().getDeclaredMethods();
        } catch (NoClassDefFoundError e) {
            logger.warn("Class not available during getDeclaredMethods", e);
            return new Method[0];
        }
    }

    public boolean isAnnotationPresent(final Class<?> additionalClassesClass) {
        return getClazz().isAnnotationPresent(additionalClassesClass);
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        return (A) (getClazz().getAnnotation(annotationClass));
    }

    public Annotation[] getAnnotations() {
        return getClazz().getAnnotations();
    }

    public int getModifiers() {
        return getClazz().getModifiers();
    }

    public boolean isMemberClass() {
        return getClazz().isMemberClass();
    }

    public String getName() {
        return !isNull() ? getClazz().getName() : null;
    }

    public Class getBaseclass() {
        return getClazz();
    }

    public String getSimpleName() {
        return !isNull() ? getClazz().getSimpleName() : null;
    }

    public boolean isNull() {
        return getClazz() == null;
    }

    public boolean isAvailable() {
        if (available == null) {
            try {
                if (!isNull())
                    getClazz().getDeclaredFields();
                available = true;
            } catch (NoClassDefFoundError e) {
                logger.warn("Class not available during getDeclaredFields", e);
                available = false;
            }
        }
        return available;
    }
}
