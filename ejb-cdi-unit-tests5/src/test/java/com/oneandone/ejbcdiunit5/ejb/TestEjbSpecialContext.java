package com.oneandone.ejbcdiunit5.ejb;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.cdi.testanalyzer.annotations.EnabledAlternatives;
import com.oneandone.cdi.testanalyzer.annotations.SutClasses;
import com.oneandone.cdi.testanalyzer.annotations.TestClasses;
import com.oneandone.cdi.tester.JUnit5Extension;
import com.oneandone.ejbcdiunit.ejbs.SingletonEJB;
import com.oneandone.ejbcdiunit5.helpers.J2eeSimTest1Factory;
import com.oneandone.ejbcdiunit5.helpers.LoggerGenerator;
import com.oneandone.ejbcdiunit5.helpers.SessionContextFactoryAlternative;

/**
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
@SutClasses({ SingletonEJB.class, J2eeSimTest1Factory.class })
@TestClasses(LoggerGenerator.class)
@EnabledAlternatives({ SessionContextFactoryAlternative.class })
public class TestEjbSpecialContext {

    @Inject
    J2eeSimTest1Factory persistenceFactory;
    @EJB
    SingletonEJB singletonEJB;

    @Produces
    @ApplicationScoped
    EntityManager createEntityManager() {
        return persistenceFactory.produceEntityManager();
    }

    @Test
    public void checkContextAlternative() {
        MatcherAssert.assertThat(singletonEJB.getPrincipal().getName(), Matchers.is(SessionContextFactoryAlternative.PRINCIPAL_NAME));
    }


}
