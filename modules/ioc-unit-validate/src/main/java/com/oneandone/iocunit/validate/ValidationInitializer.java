package com.oneandone.iocunit.validate;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.validation.ValidatorFactory;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class ValidationInitializer {

    @Inject
    ValidatorFactory validatorFactory;

    public void onStart(@Observes @Initialized(ApplicationScoped.class) Object init) {
        try {
            Context context = new InitialContext();
            context.bind("java:comp/ValidatorFactory", validatorFactory);
            context.close();
        } catch (NamingException nm) {
            throw new RuntimeException(nm);
        }
    }
}
