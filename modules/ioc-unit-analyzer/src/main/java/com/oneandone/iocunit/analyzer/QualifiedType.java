package com.oneandone.iocunit.analyzer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.Typed;
import javax.inject.Provider;
import javax.inject.Qualifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.iocunit.analyzer.reflect.IocUnitTypeUtils;

/**
 * Wraps elements found during the class-scan, that might be used as producers or injection-destinations.
 * getType() and getQualifiers() can be used for exact matching.
 *
 * @author aschoerk
 */
public class QualifiedType {
    static Logger log = LoggerFactory.getLogger(QualifiedType.class);

    private Field f;         // if not null the m, c, p and clazz are null.
    private Method m;
    private Constructor c;
    private Parameter p;
    private Class clazz;

    private Class declaringClass;

    private Set<Annotation> qualifiers;   // the qualifiers extracted from the element
    private Annotation alternativeStereotype;
    private boolean alternative;  // true if by @Alternative or alternative-stereotype designated as CDI-Alternative
    private Collection<? extends Annotation> injects = null;
    private boolean fake;
    private boolean allowParameterizedInjectedToRawtype;

    public boolean isFake() {
        return fake;
    }

    public QualifiedType(final Class clazz, boolean checkAbstract) {
        assert clazz != null;
        if (clazz.isInterface() || clazz.isAnnotation() || checkAbstract && Modifier.isAbstract(clazz.getModifiers()))
            log.error("Invalid Producer for clazz: {}", clazz);
        this.clazz = clazz;
        this.declaringClass = clazz;
        if(clazz.getAnnotation(Alternative.class) != null) {
            alternative = true;
        }
    }


    public QualifiedType(final Parameter p, final Constructor c) {
        assert p != null;
        assert c != null;
        this.p = p;
        this.c = c;
    }

    public boolean isField() {
        return f != null;
    }

    public boolean isClass() {
        return clazz != null && clazz.equals(declaringClass);
    }


    public Field getField() {
        if (!isField())
            throw new RuntimeException("expected QualifiedType to be Field");
        return f;
    }

    public QualifiedType(final Parameter p, final Method m) {
        assert p != null;
        assert m != null;
        this.p = p;
        this.m = m;
    }

    void checkGetAlternativeStereoType(Annotation[] annotations) {
        for (Annotation ann : annotations) {
            Class<? extends Annotation> annotationType = ann.annotationType();
            if(annotationType.getAnnotation(Stereotype.class) != null
               && annotationType.getAnnotation(Alternative.class) != null) {

                alternative = true;
                alternativeStereotype = ann;
                log.info("is alternative: {}", this);
            }
        }
    }

    public QualifiedType(final Method m) {
        assert m != null;
        this.m = m;
        if(m.getAnnotation(Alternative.class) != null) {
            alternative = true;
        }
        checkGetAlternativeStereoType(m.getAnnotations());
    }

    public QualifiedType(final Field f) {
        this(f, null);
    }

    public QualifiedType(final Field f, Collection<? extends Annotation> injects) {
        assert f != null;
        this.injects = injects;
        this.f = f;
        if(f.getAnnotation(Alternative.class) != null) {
            alternative = true;
        }
        checkGetAlternativeStereoType(f.getAnnotations());
    }

    private Class getRawtypeInternal() {
        Type t = getTypeInternal();
        if(t instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) t).getRawType();
        }
        else {
            return (Class<?>) t;
        }
    }

    boolean isInstance() {
        Class res = getRawtypeInternal();
        return (res.equals(Instance.class));
    }

    boolean isProvider() {
        Class res = getRawtypeInternal();
        return (res.equals(Provider.class));
    }

    Class getRawtype() {
        Class res = getRawtypeInternal();
        if(isProviderOrInstance()) {
            return getProviderOrInstanceRawType();
        }
        else {
            return res;
        }
    }

    private boolean isProviderOrInstance() {
        return isProvider() || isInstance();
    }

    private Class getProviderOrInstanceRawType() {
        ParameterizedType pType = ((ParameterizedType) getTypeInternal());
        Type t =  (pType.getActualTypeArguments()[0]);
        if(t instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) t).getRawType();
        }
        else {
            return (Class<?>) t;
        }
    }

    Set<Annotation> getQualifiers() {
        if(this.qualifiers == null) {
            Set<Annotation> tmpQualifiers = new HashSet<>();
            for (Annotation annotation : getAnnotations()) {
                if(annotation.annotationType().isAnnotationPresent(Qualifier.class)) {
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
    private Type getTypeInternal() {
        if(p != null) {
            return p.getParameterizedType();
        }
        if(m != null) {
            return m.getGenericReturnType();
        }
        if(f != null) {
            return f.getGenericType();
        }
        if(clazz != null) {
            return clazz;
        }
        throw new AssertionError();
    }

    Type getType() {
        Class res = getRawtypeInternal();
        if(isProviderOrInstance()) {
            ParameterizedType pType = ((ParameterizedType) getTypeInternal());
            return (pType.getActualTypeArguments()[0]);
        }
        else {
            return getTypeInternal();
        }

    }

    boolean hasAnnotation(Class ann) {
        if(p != null) {
            return p.getAnnotation(ann) != null;
        }
        if(m != null) {
            return m.getAnnotation(ann) != null;
        }
        if(f != null) {
            return f.getAnnotation(ann) != null;
        }
        if(clazz != null) {
            return clazz.getAnnotation(ann) != null;
        }
        throw new AssertionError();
    }

    Annotation[] getAnnotations() {
        if(p != null) {
            return p.getAnnotations();
        }
        if(m != null) {
            return m.getAnnotations();
        }
        if(f != null) {
            return f.getAnnotations();
        }
        if(clazz != null) {
            return clazz.getAnnotations();
        }
        throw new AssertionError();
    }

    public Class getDeclaringClass() {
        if(p != null) {
            return m != null ? m.getDeclaringClass() : c.getDeclaringClass();
        }
        if(m != null) {
            return m.getDeclaringClass();
        }
        if(f != null) {
            return f.getDeclaringClass();
        }
        if(clazz != null) {
            return declaringClass;
        }
        return null;
    }

    public String getMemberName() {
        if(p != null) {
            return p.getName();
        }
        if(m != null) {
            return m.getName();
        }
        if(f != null) {
            return f.getName();
        }
        return null;
    }


    public Annotation getAlternativeStereotype() {
        return alternativeStereotype;
    }

    public boolean isAlternative() {
        return alternative;
    }

    public boolean qualifiersInjectableIn(QualifiedType q) {
        return IocUnitTypeUtils.equals(getType(), q.getType()) && qualifiersMatchFromToInject(this, q);
    }

    public boolean isRawType() {
        return isRawType(getType());
    }

    public static boolean isRawType(Type type) {
        if (type == null || type.equals(Object.class))
            return false;
        if (type instanceof ParameterizedType) {
            return isArrayOfUnboundedTypeVariablesOrObjects(((ParameterizedType)type).getActualTypeArguments());
        } else {
            if (type instanceof Class)
                if (((Class)type).getTypeParameters().length > 0)
                    return isArrayOfUnboundedTypeVariablesOrObjects(((Class)type).getTypeParameters());
                else
                    return isRawType(((Class)type).getGenericSuperclass());
            else
                return false;
        }
    }

    /**
     * Determines whether the given array only contains unbounded type variables or Object.class.
     *
     * @param types the given array of types
     * @return true if and only if the given array only contains unbounded type variables or Object.class
     */
    private static boolean isArrayOfUnboundedTypeVariablesOrObjects(Type[] types) {
        for (Type type : types) {
            if (Object.class.equals(type)) {
                continue;
            }
            if (type instanceof TypeVariable<?>) {
                Type[] bounds = ((TypeVariable<?>) type).getBounds();
                if (bounds == null || bounds.length == 0 || (bounds.length == 1 && Object.class.equals(bounds[0]))) {
                    continue;
                }
            }
            return false;
        }
        return true;
    }

    public boolean isAssignableTo(QualifiedType inject) {
        boolean assignable = IocUnitTypeUtils.isAssignable(getType(), inject.getType())
                             && qualifiersMatchFromToInject(this, inject);
        if(!assignable) {
            return assignable;
        }
        else {
            Typed typedAnnotation = (Typed)getRawtype().getAnnotation(Typed.class);
            if (typedAnnotation != null) {
                boolean oneIsAssignable = false;
                for (Class<?> aClass: typedAnnotation.value()) {
                    if(IocUnitTypeUtils.isAssignable(aClass, inject.getType()) && IocUnitTypeUtils.isAssignable(inject.getType(), aClass)) {
                        oneIsAssignable = true;
                    }
                }
                if(!oneIsAssignable) {
                    return false;
                }
            }

            if(inject.isInstance()) {
                return true;
            }

            // my assign to rawtype but not the other way round
            return inject.isRawType() || !isRawType();
        }

    }

    static public boolean qualifiersInjectableIn(Set<Annotation> produced, Set<Annotation> to) {
        if(to.isEmpty()) {
            if(produced.isEmpty()
               || produced
                    .stream()
                    .anyMatch(a -> a.annotationType().getName().equals("javax.enterprise.inject.Default"))) {
                return true;
            }
        }
        if(to.size() == 1 && to.iterator().next().annotationType().getName().equals("javax.enterprise.inject.Any")) {
            return true;
        }
        Set<Annotation> toFiltered = to
                .stream()
                .filter(a -> !(a.annotationType().getName().equals("javax.enterprise.inject.Any")
                               || a.annotationType().getName().equals("javax.enterprise.inject.New")))
                .collect(Collectors.toSet());
        if(produced.containsAll(toFiltered) && !to.isEmpty()) {
            return true;
        }
        if(produced.size() == 1 && produced.iterator().next().annotationType().getName().equals("javax.enterprise.inject.Default")) {
            if(toFiltered.isEmpty()) // to is Default already checked
            {
                return true;
            }
        }
        Set<String> producednames = produced.stream().map(a -> a.annotationType().getName()).filter(n -> !n.equals("javax.enterprise.inject.Default"))
                .collect(Collectors.toSet());
        if(producednames.size() == 1 && producednames.contains("javax.inject.Named")) {
            if(toFiltered.isEmpty()
               || toFiltered.size() == 1
                  && toFiltered.iterator().next().annotationType()
                          .getName().equals("javax.enterprise.inject.Default")) // to
            // is
            // Default
            // already
            // checked
            {
                return true;
            }
        }
        if(toFiltered.isEmpty()) // produced can not be empty, Default or Named anymore
        {
            return false;
        }
        if(produced.containsAll(toFiltered)) {
            return true;
        }
        if(produced.size() > 0) {
            return false;
        }
        return false;
    }


    public static Boolean qualifiersMatchFromToInject(final QualifiedType qp, final QualifiedType qi) {
        final Set<Annotation> qiqualifiers = qi.getQualifiers();
        final Set<Annotation> qpqualifiers = qp.getQualifiers();
        return qualifiersInjectableIn(qpqualifiers, qiqualifiers);
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
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

    @Override
    public String toString() {
        Class declaringClassL = getDeclaringClass();
        String memberName = getMemberName();
        return "QualifiedType{" +
               (memberName != null ? " name=" + memberName : "") +
               " type=" + getType().getTypeName() +
               (declaringClassL != null ? " declared by=" + declaringClassL.getSimpleName() : "") +
               ", qualifiers=" + qualifiers +
               ", altStereotype=" + alternativeStereotype +
               ", alt=" + alternative +
               ", providerOrInstance=" + isProviderOrInstance() +
               '}';
    }

    public void setAlternative() {
        this.alternative = true;
    }

    public QualifiedType fake() {
        this.fake = true;
        return this;
    }
}
