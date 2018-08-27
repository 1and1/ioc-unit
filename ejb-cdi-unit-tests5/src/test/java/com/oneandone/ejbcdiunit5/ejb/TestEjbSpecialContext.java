package com.oneandone.ejbcdiunit5.ejb;

import com.oneandone.ejbcdiunit.ejbs.SingletonEJB;
import com.oneandone.ejbcdiunit5.JUnit5Extension;
import com.oneandone.ejbcdiunit5.helpers.J2eeSimTest1Factory;
import com.oneandone.ejbcdiunit5.helpers.LoggerGenerator;
import com.oneandone.ejbcdiunit5.helpers.SessionContextFactoryAlternative;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
@AdditionalClasses({ SingletonEJB.class, J2eeSimTest1Factory.class, LoggerGenerator.class})
@ActivatedAlternatives({SessionContextFactoryAlternative.class})
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
