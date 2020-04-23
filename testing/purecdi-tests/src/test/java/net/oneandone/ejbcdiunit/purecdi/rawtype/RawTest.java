package net.oneandone.ejbcdiunit.purecdi.rawtype;

import org.jboss.weld.exceptions.DeploymentException;
import org.junit.Test;

import net.oneandone.ejbcdiunit.purecdi.WeldStarterTestBase;

/**
 * @author aschoerk
 */
public class RawTest extends WeldStarterTestBase {
    @Test
    public void testSimpleRaw() {
        setBeanClasses(RawListProducer.class, RawListContainer.class, ParameterizedProducer.class);
        start();
    }

    @Test(expected = DeploymentException.class)
    public void testRawSuperWithSubContainer() {
        setBeanClasses(RawListProducer.class, RawListSubContainer.class, ParameterizedProducer.class);
        start();
    }

    @Test
    public void canInjectSubclassOfRawIntoRawByProducer() {
        setBeanClasses(RawListSubProducer.class, RawListContainer.class, ParameterizedProducer.class);
        start();
    }

    @Test
    public void canInjectSubclassOfRawIntoRawByItself() {
        setBeanClasses(RawListSub.class, RawListContainer.class, ParameterizedProducer.class);
        start();
    }

    @Test(expected = DeploymentException.class)
    public void cannotInjectSubclassOfRawIntoRawByProducerIfRawProducerIncluded() {
        setBeanClasses(RawListSubProducer.class, RawListProducer.class, RawListContainer.class, ParameterizedProducer.class);
        start();
    }

    @Test(expected = DeploymentException.class)
    public void cannotInjectSubclassOfRawIntoRawByItselfIfRawProducerIncluded() {
        setBeanClasses(RawListSub.class, RawListProducer.class, RawListContainer.class, ParameterizedProducer.class);
        start();
    }

    @Test
    public void canInjectSubclassOfRawIntoRawListSubByProducerIfRawProducerIncluded() {
        setBeanClasses(RawListSubProducer.class, RawListProducer.class, RawListSubContainer.class, ParameterizedProducer.class);
        start();
    }

    @Test
    public void canInjectSubclassOfRawIntoRawListSubByItselfIfRawProducerIncluded() {
        setBeanClasses(RawListSub.class, RawListProducer.class, RawListSubContainer.class, ParameterizedProducer.class);
        start();
    }

    @Test
    public void testSubRaw() {
        setBeanClasses(RawListSubProducer.class, RawListContainer.class, ParameterizedProducer.class);
        start();
    }

    @Test
    public void testSimpleParameterized() {
        setBeanClasses(RawListProducer.class, ParameterizedListContainer.class, ParameterizedProducer.class);
        start();
    }

    @Test(expected = DeploymentException.class)
    public void testSubParameterizedAmbiguus() {
        setBeanClasses(RawListProducer.class, ParameterizedListContainer.class, StringListProducer.class, ParameterizedProducer.class);
        start();
    }

    @Test
    public void testSubParameterized() {
        setBeanClasses(RawListProducer.class, ParameterizedListContainer.class, StringListProducer.class);
        start();
    }

    @Test
    public void canInjectStringListInParameterizedList() {
        setBeanClasses(RawListSubProducer.class, RawListProducer.class, ParameterizedListContainer.class, StringListProducer.class);
        start();
    }

    @Test
    public void canInjectStringListInParameterized() {
        setBeanClasses(RawListSubProducer.class, RawListProducer.class, ParameterizedStringListContainer.class, StringListProducer.class);
        start();
    }


}
