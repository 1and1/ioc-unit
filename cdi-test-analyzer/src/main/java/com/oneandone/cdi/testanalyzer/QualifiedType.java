package com.oneandone.cdi.testanalyzer;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Stereotype;
import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Wraps elements found during the class-scan, that might be used as producers or injection-destinations.
 * getType() and getQualifiers() can be used for exact matching.
 *
 * @author aschoerk
 */
class QualifiedType {

    private Field f;         // if not null the m, c, p and clazz are null.
    private Method m;
    private Constructor c;
    private Parameter p;
    private Class clazz;

    private Set<Annotation> qualifiers;   // the qualifiers extracted from the element
    private Annotation alternativeStereotype;
    private boolean alternative;  // true if by @Alternative or alternative-stereotype designated as CDI-Alternative

    public QualifiedType(final Class clazz) {
        assert clazz != null;
        this.clazz = clazz;
        if (clazz.getAnnotation(Alternative.class) != null)
            alternative = true;
    }

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

    void checkGetAlternativeStereoType(Annotation ann) {
        if (ann != null) {
            Class<? extends Annotation> aClass = ann.annotationType();
            if (aClass.getAnnotation(Stereotype.class) != null && aClass.getAnnotation(Alternative.class) != null) {
                alternative = true;
                alternativeStereotype = ann;
            }
        }
    }

    public QualifiedType(final Method m) {
        assert m != null;
        this.m = m;
        if (m.getAnnotation(Alternative.class) != null)
            alternative = true;
        checkGetAlternativeStereoType(m.getAnnotation(Stereotype.class));

    }

    public QualifiedType(final Field f) {
        assert f != null;
        this.f = f;
        if (f.getAnnotation(Alternative.class) != null)
            alternative = true;
        checkGetAlternativeStereoType(f.getAnnotation(Stereotype.class));
    }

    Class getRawtype() {
        Type t = getType();
        if (t instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) t).getRawType();
        } else {
            return (Class<?>) t;
        }
    }

    Set<Annotation> getQualifiers() {
        if (this.qualifiers == null) {
            Set<Annotation> tmpQualifiers = new HashSet<>();
            for (Annotation annotation : getAnnotations()) {
                if (annotation.annotationType().isAnnotationPresent(Qualifier.class)) {
                    tmpQualifiers.add(annotation);
                }
            }
            this.qualifiers = tmpQualifiers;
        }
        return this.qualifiers;
    }

    /**
     * The type that can be used for matching. Can be class or parameterized Type.
     *
     * @return the type that can be used for matching.
     */
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
        if (clazz != null)
            return clazz;
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
        if (clazz != null)
            return clazz.getAnnotation(ann) != null;
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
        if (clazz != null)
            return clazz.getAnnotations();
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
        if (clazz != null)
            return clazz;
        return null;
    }

    public Annotation getAlternativeStereotype() {
        return alternativeStereotype;
    }

    public boolean isAlternative() {
        return alternative;
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
                Objects.equals(p, that.p) &&
                Objects.equals(clazz, that.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(f, m, c, p, clazz);
    }
}
