package com.oneandone.iocunit.validate;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.validation.ValidatorFactory;

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
