package com.oneandone.iocunitejb.ejbs;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class CDIClass {
    @EJB
    StatelessEJB statelessEJB;

    @EJB
    SingletonEJB singletonEJB;

    public StatelessEJB getStatelessEJB() {
        return statelessEJB;
    }

    public SingletonEJB getSingletonEJB() {
        return singletonEJB;
    }

    /**
     * call bothe stateless and singleton
     */
    public void doSomething() {
        statelessEJB.method1();
        singletonEJB.methodCallUsingSessionContext();
    }
}
