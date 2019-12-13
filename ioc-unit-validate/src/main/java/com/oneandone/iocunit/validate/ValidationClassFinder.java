package com.oneandone.iocunit.validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aschoerk
 */
public class ValidationClassFinder {
    private static Logger logger = LoggerFactory.getLogger(ValidationClassFinder.class);
    public static Class getInterceptor() {
        try {
            return Class.forName("org.hibernate.validator.internal.cdi.interceptor.ValidationInterceptor");
        } catch (ClassNotFoundException|ClassFormatError e) {
            try {
                return Class.forName("org.hibernate.validator.cdi.internal.interceptor.ValidationInterceptor");
            } catch (ClassNotFoundException ex) {
                logger.error("no validator interceptor found");
                return null;
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
                logger.error("no validator annotation for MethodValidated found");
                return null;
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
                logger.error("no validator annotation for ConstructorValidated found");
                return null;
            }
        }

    }
}
