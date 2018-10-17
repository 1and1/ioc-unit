package net.oneandone.ejbcdiunit.tests.notavailable;

/**
 * @author aschoerk
 */
public class InjectedBean {
    public InjectedBean() throws ShouldNotBeCreatedException {
        throw new ShouldNotBeCreatedException();
    }
}
