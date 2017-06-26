package com.oneandone.ejbcdiunit;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hamcrest.Matchers;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.ejbcdiunit.ejbs.SingletonEJB;
import com.oneandone.ejbcdiunit.helpers.J2eeSimTest1Factory;
import com.oneandone.ejbcdiunit.helpers.LoggerGenerator;
import com.oneandone.ejbcdiunit.helpers.SessionContextFactoryAlternative;

/**
 * @author aschoerk
 */
@RunWith(EjbUnitRunner.class)
@AdditionalClasses({ SingletonEJB.class, J2eeSimTest1Factory.class, LoggerGenerator.class})
@ActivatedAlternatives({SessionContextFactoryAlternative.class})
public class TestEjbSpecialContext {

    @Inject
    J2eeSimTest1Factory persistenceFactory;

    @Produces
    @ApplicationScoped
    EntityManager createEntityManager() {
        return persistenceFactory.produceEntityManager();
    }

    @EJB
    SingletonEJB singletonEJB;

    @Test
    public void checkContextAlternative() {
        Assert.assertThat(singletonEJB.getPrincipal().getName(), Matchers.is(SessionContextFactoryAlternative.PRINCIPAL_NAME));
    }


}
