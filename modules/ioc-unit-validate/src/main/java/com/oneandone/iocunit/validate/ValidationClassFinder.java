package com.oneandone.iocunit.validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aschoerk
 */
public class ValidationClassFinder {
    private static Logger logger = LoggerFactory.getLogger(ValidationClassFinder.class);
    public static Class getInterceptor() {
        // Hibernate Validator 9.x (Jakarta Validation 3.1+)
        try {
            return Class.forName("org.hibernate.validator.cdi.interceptor.spi.ValidationInterceptor");
        } catch (ClassNotFoundException|ClassFormatError e) {
            // Hibernate Validator 8.x / 6.x
            try {
                return Class.forName("org.hibernate.validator.internal.cdi.interceptor.ValidationInterceptor");
            } catch (ClassNotFoundException|ClassFormatError ex) {
                try {
                    return Class.forName("org.hibernate.validator.cdi.internal.interceptor.ValidationInterceptor");
                } catch (ClassNotFoundException exc) {
                    logger.error("no validator interceptor found");
                    return null;
                }
            }
        }
    }

    public static Class getMethodValidatedAnnotation() {
        // Hibernate Validator 9.x (Jakarta Validation 3.1+)
        try {
            return Class.forName("org.hibernate.validator.cdi.interceptor.internal.MethodValidated");
        } catch (ClassNotFoundException e) {
            // Hibernate Validator 8.x / 6.x
            try {
                return Class.forName("org.hibernate.validator.internal.cdi.interceptor.MethodValidated");
            } catch (ClassNotFoundException ex) {
                try {
                    return Class.forName("org.hibernate.validator.cdi.internal.interceptor.MethodValidated");
                } catch (ClassNotFoundException exc) {
                    logger.error("no validator annotation for MethodValidated found");
                    return null;
                }
            }
        }

    }
    public static Class getConstructorValidatedAnnotation() {
        // Hibernate Validator 9.x removed the dedicated ConstructorValidated annotation;
        // method validation now uses MethodValidated for both methods and constructors.
        try {
            return Class.forName("org.hibernate.validator.internal.cdi.interceptor.ConstructorValidated");
        } catch (ClassNotFoundException e) {
            return getMethodValidatedAnnotation();
        }

    }
}
