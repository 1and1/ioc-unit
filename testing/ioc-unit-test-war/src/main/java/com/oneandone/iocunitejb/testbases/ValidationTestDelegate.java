package com.oneandone.iocunitejb.testbases;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.junit.Test;

import com.oneandone.iocunitejb.validation.ValidatedApplicationScoped;
import com.oneandone.iocunitejb.validation.ValidatedEjb;

/**
 * @author aschoerk
 */
public class ValidationTestDelegate {

    @EJB
    ValidatedEjb validatedEjb;

    @Inject
    ValidatedApplicationScoped validatedApplicationScoped;


    @Test
    public void checkValidationInEjb() throws Exception {
        try {
            validatedEjb.callWithoutNull(null);
            throw new RuntimeException("Expected @NotNull-Validation to produce ValidationException");
        } catch (EJBException ex) {
            handleEjbConstraintViolation(ex);
        }
    }

    @Test
    public void checkValidationInNotSupported() throws Exception {
        try {
            validatedEjb.callWithoutNullSinTransaction(null);
            throw new RuntimeException("Expected @NotNull-Validation to produce ValidationException");
        } catch (EJBException ex) {
            handleEjbConstraintViolation(ex);
        }
    }

    private void handleEjbConstraintViolation(final EJBException ex) throws Exception {
        final Class<? extends Exception> exceptionClass = ex.getCausedByException().getClass();
        if(!exceptionClass.equals(ValidationException.class) && !exceptionClass.equals(ConstraintViolationException.class)) {
            throw ex.getCausedByException();
        }
    }

    @Test
    public void checkValidationAppScopedInTransaction() throws Exception {
        try {
            validatedApplicationScoped.callWithoutNull(null);
            throw new RuntimeException("Expected @NotNull-Validation to produce ValidationException");
        } catch (ConstraintViolationException ex) {
        }
    }

    @Test
    public void checkValidationAppScopedWOTransaction() throws Exception {
        try {
            validatedApplicationScoped.callWithoutNullSinTransaction(null);
            throw new RuntimeException("Expected @NotNull-Validation to produce ValidationException");
        } catch (ConstraintViolationException e) {

        }
    }
}
