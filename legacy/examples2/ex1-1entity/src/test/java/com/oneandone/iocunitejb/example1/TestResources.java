package com.oneandone.iocunitejb.example1;

import jakarta.enterprise.inject.Produces;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * @author aschoerk
 */
public class TestResources {
    @Produces
    @javax.enterprise.context.ApplicationScoped
    Validator getInstanceValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return factory.getValidator();
    }


}
