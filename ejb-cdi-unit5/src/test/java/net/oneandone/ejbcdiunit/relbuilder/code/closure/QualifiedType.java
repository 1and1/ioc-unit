package net.oneandone.ejbcdiunit.relbuilder.code.closure;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * @author aschoerk
 */
class QualifiedType {
    private Field f;
    private Method m;
    private Constructor c;
    private Parameter p;

    public QualifiedType(final Parameter p, final Constructor c) {
        assert p != null;
        assert c != null;
        this.p = p;
        this.c = c;
    }

    public QualifiedType(final Parameter p, final Method m) {
        assert p != null;
        assert m != null;
        this.p = p;
        this.m = m;
    }

    public QualifiedType(final Method m) {
        assert m != null;
        this.m = m;
    }

    public QualifiedType(final Field f) {
        assert f != null;
        this.f = f;
    }

    Class getRawtype() {
        Type t = getType();
        if (t instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) t).getRawType();
        } else {
            return (Class<?>) t;
        }
    }

    Type getType() {
        if (p != null) {
            return p.getParameterizedType();
        }
        if (m != null) {
            return m.getGenericReturnType();
        }
        if (f != null) {
            return f.getGenericType();
        }
        throw new AssertionError();
    }

    boolean hasAnnotation(Class ann) {
        if (p != null) {
            return p.getAnnotation(ann) != null;
        }
        if (m != null) {
            return m.getAnnotation(ann) != null;
        }
        if (f != null) {
            return f.getAnnotation(ann) != null;
        }
        throw new AssertionError();
    }

    Annotation[] getAnnotations() {
        if (p != null) {
            return p.getAnnotations();
        }
        if (m != null) {
            return m.getAnnotations();
        }
        if (f != null) {
            return f.getAnnotations();
        }
        throw new AssertionError();
    }

    public Class getDeclaringClass() {
        if (p != null) {
            return m != null ? m.getDeclaringClass() : c.getDeclaringClass();
        }
        if (m != null) {
            return m.getDeclaringClass();
        }
        if (f != null) {
            return f.getDeclaringClass();
        }
        return null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        QualifiedType that = (QualifiedType) o;
        return Objects.equals(f, that.f) &&
                Objects.equals(m, that.m) &&
                Objects.equals(c, that.c) &&
                Objects.equals(p, that.p);
    }

    @Override
    public int hashCode() {
        return Objects.hash(f, m, c, p);
    }
}
