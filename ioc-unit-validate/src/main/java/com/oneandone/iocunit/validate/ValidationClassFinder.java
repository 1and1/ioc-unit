package com.oneandone.iocunit.validate;

/**
 * @author aschoerk
 */
public class ValidationClassFinder {
    public static Class getInterceptor() {
        try {
            return Class.forName("org.hibernate.validator.internal.cdi.interceptor.ValidationInterceptor");
        } catch (ClassNotFoundException e) {
            try {
                return Class.forName("org.hibernate.validator.cdi.internal.interceptor.ValidationInterceptor");
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException("No Hibernate Validater available");
            }
        }
    }

    public static Class getMethodValidatedAnnotation() {
        try {
            return Class.forName("org.hibernate.validator.internal.cdi.interceptor.MethodValidated");
        } catch (ClassNotFoundException e) {
            try {
                return Class.forName("org.hibernate.validator.cdi.internal.interceptor.MethodValidated");
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException("No Hibernate Validater available");
            }
        }

    }
    public static Class getConstructorValidatedAnnotation() {
        try {
            return Class.forName("org.hibernate.validator.internal.cdi.interceptor.ConstructorValidated");
        } catch (ClassNotFoundException e) {
            try {
                return Class.forName("org.hibernate.validator.internal.cdi.interceptor.MethodValidated");
            } catch (ClassNotFoundException ex) {
                return null;
            }
        }

    }
}
