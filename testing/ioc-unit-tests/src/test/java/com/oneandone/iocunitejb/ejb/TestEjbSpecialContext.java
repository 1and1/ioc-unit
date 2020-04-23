package com.oneandone.iocunitejb.ejb;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.analyzer.annotations.EnabledAlternatives;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunitejb.ejbs.SingletonEJB;
import com.oneandone.iocunitejb.helpers.J2eeSimTest1Factory;
import com.oneandone.iocunitejb.helpers.LoggerGenerator;
import com.oneandone.iocunitejb.helpers.SessionContextFactoryAlternative;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@TestClasses({ SingletonEJB.class, J2eeSimTest1Factory.class, LoggerGenerator.class })
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
        Assert.assertThat(singletonEJB.getPrincipal().getName(), Matchers.is(SessionContextFactoryAlternative.PRINCIPAL_NAME));
    }


}
