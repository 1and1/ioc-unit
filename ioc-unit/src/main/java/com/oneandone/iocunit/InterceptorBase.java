package com.oneandone.iocunit;

import javax.interceptor.InvocationContext;
import java.lang.annotation.Annotation;

public class InterceptorBase {
    static ThreadLocal<Integer> level = new ThreadLocal<>();

    static public int getLevel() {
        Integer actLevel = level.get();
        if (actLevel == null) {
            level.set(0);
            actLevel = 0;
        }
        return actLevel;
    }

    static public void incLevel() {
        level.set(getLevel() + 1);
    }

    static public void decLevel() {
        level.set(getLevel() - 1);
    }

    protected Class<?> getTargetClass(InvocationContext ctx) {
        final Object target = ctx.getTarget();
        if (target == null)
            return null;
        Class<? extends Object> res = target.getClass();
        if (res.getName().endsWith("WeldSubclass"))
            return res.getSuperclass();
        else
            return res;

    }

    protected <T extends Annotation> T findAnnotation(Class<?> declaringClass, Class<T> annotationType) {
        if (declaringClass == null || declaringClass.equals(Object.class))
            return null;
        T annotation = declaringClass.getAnnotation(annotationType);
        if (annotation == null) {
            return findAnnotation(declaringClass.getSuperclass(), annotationType);
        } else {
            return annotation;
        }
    }
}
