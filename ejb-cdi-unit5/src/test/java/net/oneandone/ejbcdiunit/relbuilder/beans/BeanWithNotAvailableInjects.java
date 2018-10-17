package net.oneandone.ejbcdiunit.relbuilder.beans;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import net.oneandone.ejbcdiunit.tests.notavailable.NotAvailableInjectedBean;

/**
 * @author aschoerk This bean can not be created during test at runtime, since purecdi-tests is defined as provided nevertheless this Bean must be
 *         Injectable during analyzing, so that beans using it can be replaced by alternatives.
 */
public class BeanWithNotAvailableInjects {
    @Inject
    NotAvailableInjectedBean notAvailableInjectedBean;

    @Inject
    BeanWithNotAvailableInjects(NotAvailableInjectedBean notAvailableInjectedBean1) {

    }

    @Produces
    TestBeanWithInjectedField testBeanWithInjectedFieldProducer(NotAvailableInjectedBean notAvailableInjectedBean2,
            TestBeanWithInjectedField testBeanWithInjectedField) {

        return testBeanWithInjectedField;
    }
}
