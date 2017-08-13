package com.oneandone.ejbcdiunit.ejbs;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;

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
