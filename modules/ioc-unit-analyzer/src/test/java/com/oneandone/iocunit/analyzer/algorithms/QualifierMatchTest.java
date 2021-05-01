package com.oneandone.iocunit.analyzer.algorithms;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.New;
import javax.inject.Named;
import javax.inject.Qualifier;

import org.junit.jupiter.api.Test;

import com.oneandone.iocunit.analyzer.QualifiedType;

/**
 * @author aschoerk
 */
public class QualifierMatchTest {

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
    @interface Q1 {

    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
    @interface Q2 {

    }

    Annotation a(String name) {
        Class<? extends QualifierMatchTest> c = this.getClass();
        return getAnnotation(name, c);
    }

    private static Annotation getAnnotation(final String name, final Class<? extends QualifierMatchTest> c) {
        try {
            final Method declaredMethod = c.getDeclaredMethod(name);
            return declaredMethod.getDeclaredAnnotations()[0];
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("ex");
        }
    }

    @Q1
    void q1() {}

    @Q2
    void q2() {}

    @Default
    void def() {}

    @Any
    void any() {}

    @Named
    void named() {}

    @Named("name")
    void namedName() {}

    @New
    void nw() {}

    static Set<Annotation> cloneAdd(Set<Annotation> set, Annotation... ann) {
        Set<Annotation> result = new HashSet<>(set);
        for (Annotation a : ann)
            result.add(a);
        return result;
    }

    static boolean match(Set<Annotation> produced, Set<Annotation> to) {
        return QualifiedType.qualifiersInjectableIn(produced, to)
                && QualifiedType.qualifiersInjectableIn(produced, cloneAdd(to, getAnnotation("nw", QualifierMatchTest.class)));
    }

    Set<Annotation> create(String... annNames) {
        Set<Annotation> result = new HashSet<>();
        for (String annName : annNames) {
            result.add(a(annName));
        }
        return result;
    }

    @Test
    public void test() {
        Set<Annotation> q1 = create("q1");
        Set<Annotation> q2 = create("q2");
        Set<Annotation> any = create("any");
        Set<Annotation> def = create("def");
        Set<Annotation> defq1 = create("def", "q1");
        Set<Annotation> q1q2 = create("q1", "q2");
        Set<Annotation> q1any = create("q1", "any");
        Set<Annotation> q1any2 = create("q1", "any");
        Set<Annotation> q1defany = create("q1", "any");
        Set<Annotation> empty = create();
        Set<Annotation> named = create("named");
        Set<Annotation> namedName = create("namedName");
        Set<Annotation> named_namedName = create("named", "namedName");

        assertTrue(match(q1, q1));
        assertTrue(match(defq1, def));
        assertTrue(match(defq1, empty));
        assertTrue(match(q1, any));
        assertFalse(match(q1, empty));
        assertFalse(match(q1, named));
        assertTrue(match(q1q2, q1));
        assertTrue(match(q1q2, q2));
        assertFalse(match(q1q2, empty));
        assertTrue(match(empty, empty));
        assertTrue(match(def, empty));
        assertTrue(match(def, def));
        assertTrue(match(named, def));
        assertTrue(match(named, empty));
        assertTrue(match(named, named));
        assertTrue(match(namedName, namedName));
        assertTrue(match(named_namedName, namedName));
        assertTrue(match(named_namedName, named));
        assertTrue(match(named_namedName, def));
        assertTrue(match(named_namedName, any));
        assertTrue(match(named_namedName, empty));
        assertTrue(match(q1, q1any));
        assertTrue(match(q1any, q1));
        assertTrue(match(q1any2, q1any));
        assertFalse(match(any, empty));
        assertFalse(match(any, def));
        assertFalse(match(q2, q1any));
        assertFalse(match(empty, q1any));
        assertFalse(match(def, q1any));
        assertFalse(match(named, q1any));
        assertFalse(match(empty, q1defany));
        assertFalse(match(def, q1defany));
        assertFalse(match(named, q1defany));
        assertFalse(match(named, namedName));
        assertFalse(match(namedName, named));

    }


}
