package com.oneandone.iocunitejb.example1;

import javax.enterprise.inject.Produces;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

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
