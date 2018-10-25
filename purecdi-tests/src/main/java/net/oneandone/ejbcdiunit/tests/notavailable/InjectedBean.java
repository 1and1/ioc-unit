package net.oneandone.ejbcdiunit.tests.notavailable;

/**
 * @author aschoerk
 */
public class InjectedBean {
    public InjectedBean() throws ShouldInMavenNotBeCreatedException {
        throw new ShouldInMavenNotBeCreatedException();
    }
}
