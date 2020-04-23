package com.oneandone.iocunit.validate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Set;

import javax.enterprise.inject.Produces;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;

/**
 * @author aschoerk
 */
public class ValidationInitializer {

    public static class CombinedValidators implements Validator, ExecutableValidator {
        Validator validator;
        ExecutableValidator executableValidator;


        public CombinedValidators() {
        }

        @Override
        public <T> Set<ConstraintViolation<T>> validateParameters(final T t, final Method method, final Object[] objects, final Class<?>... classes) {
            return executableValidator.validateParameters(t, method, objects, classes);
        }

        @Override
        public <T> Set<ConstraintViolation<T>> validateReturnValue(final T t, final Method method, final Object o, final Class<?>... classes) {
            return executableValidator.validateReturnValue(t, method, o, classes);
        }

        @Override
        public <T> Set<ConstraintViolation<T>> validateConstructorReturnValue(final Constructor<? extends T> constructor, final T t, final Class<?>... classes) {
            return executableValidator.validateConstructorReturnValue(constructor, t, classes);
        }

        public CombinedValidators(final Validator validator, final ExecutableValidator executableValidator) {
            this.validator = validator;
            this.executableValidator = executableValidator;
        }

        @Override
        public <T> Set<ConstraintViolation<T>> validateConstructorParameters(final Constructor<? extends T> constructor, final Object[] objects, final Class<?>... classes) {
            return executableValidator.validateConstructorParameters(constructor, objects, classes);
        }

        @Override
        public ExecutableValidator forExecutables() {
            return validator.forExecutables();
        }

        @Override
        public <T> Set<ConstraintViolation<T>> validate(final T t, final Class<?>... classes) {
            return validator.validate(t, classes);
        }

        @Override
        public <T> Set<ConstraintViolation<T>> validateProperty(final T t, final String s, final Class<?>... classes) {
            return validator.validateProperty(t, s, classes);
        }

        @Override
        public <T> Set<ConstraintViolation<T>> validateValue(final Class<T> aClass, final String s, final Object o, final Class<?>... classes) {
            return validator.validateValue(aClass, s, o, classes);
        }

        @Override
        public BeanDescriptor getConstraintsForClass(final Class<?> aClass) {
            return validator.getConstraintsForClass(aClass);
        }

        @Override
        public <T> T unwrap(final Class<T> aClass) {
            return validator.unwrap(aClass);
        }
    }

    @Produces
    @javax.enterprise.context.ApplicationScoped
    CombinedValidators getInstanceValidator() {
        javax.validation.ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        return new CombinedValidators(validator, validator.forExecutables());
    }
}
