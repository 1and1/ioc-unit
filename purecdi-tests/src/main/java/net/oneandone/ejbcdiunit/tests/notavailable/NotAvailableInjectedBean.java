package net.oneandone.ejbcdiunit.tests.notavailable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author aschoerk Bean used in tests for complilation, but not available during tests.
 */
@ApplicationScoped
public class NotAvailableInjectedBean {

    @Inject
    InjectedBean injectedBean;

    public NotAvailableInjectedBean() throws ShouldInMavenNotBeCreatedException {
        throw new ShouldInMavenNotBeCreatedException();
    }
}
