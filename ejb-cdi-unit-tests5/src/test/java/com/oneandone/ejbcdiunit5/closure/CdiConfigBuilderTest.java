package com.oneandone.ejbcdiunit5.closure;

import com.oneandone.ejbcdiunit.weldstarter.WeldStarterTestBase;
import com.oneandone.ejbcdiunit2.closure.CdiConfigBuilder;
import com.oneandone.ejbcdiunit2.closure.InitialConfiguration;
import com.oneandone.ejbcdiunit2.closure.annotations.EnabledAlternatives;
import com.oneandone.ejbcdiunit2.closure.annotations.SutClasses;
import com.oneandone.ejbcdiunit2.closure.annotations.SutPackages;
import org.jglue.cdiunit.ProducesAlternative;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.net.MalformedURLException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author aschoerk
 */
public class CdiConfigBuilderTest extends WeldStarterTestBase {

    @BeforeEach
    public void beforeEach() {
        cfg = new InitialConfiguration();
    }

    InitialConfiguration cfg = new InitialConfiguration();

    public void initialClasses(Class<?>... classes) {
        cfg.initialClasses.addAll(Arrays.asList(classes));
    }

    public void enabledAlternatives(Class<?>... classes) {
        cfg.enabledAlternatives.addAll(Arrays.asList(classes));
    }

    public void testClass(Class clazz) {
        cfg.testClass = clazz;
    }


    @AfterEach
    public void afterEach() {
        tearDown();
    }


    static class DummyBean {

    }

    static class Bean {
        @Inject
        DummyBean dummyBean;
    }

    static class TestResources {
        @Produces
        DummyBean factory() {
            return new DummyBean();
        }
    }

    static class TestMocks {
        // @Produces
        @Mock // , extension not available yet
        DummyBean dummyBeanMock; // = Mockito.mock(DummyBean.class);
    }


    @SutClasses(TestResources.class)
    static class BeanWithProducer {
        @Inject
        DummyBean dummyBean;
    }


    @SutClasses({ TestMocks.class })
    static class BeanWithProducedMock {
        @Inject
        DummyBean dummyBean;
    }

    @SutPackages({ TestMocks.class })
    static class BeanWithSutPackages {
        @Inject
        DummyBean dummyBean;
    }




    static class BeanWith2Inner {

        static class InnerDummy extends DummyBean {

        }

        static class InnerDummy2 extends DummyBean {

        }

        @Inject
        DummyBean dummyBean;
    }




    @Test
    public void testDummy() {
        assertTrue(true);
    }

    @Test
    public void testSimple1() throws MalformedURLException {
        initialClasses(DummyBean.class, Bean.class);
        configureAndStart();
        assertNotNull(selectGet(Bean.class).dummyBean);
    }

    @Test
    public void testSimplePart() throws MalformedURLException {
        Assertions.assertThrows(RuntimeException.class, () -> {
            initialClasses(Bean.class);
            configureAndStart();
        });
    }

    static class BeanWithInner {
        static class InnerDummy extends DummyBean {
        }

        @Inject
        DummyBean dummyBean;
    }

    @Test
    public void testSimpleWithInner() throws MalformedURLException {
        initialClasses(BeanWithInner.class);
        configureAndStart();
        assertNotNull(selectGet(BeanWithInner.class).dummyBean);
        assertEquals(selectGet(BeanWithInner.class).dummyBean.getClass(), BeanWithInner.InnerDummy.class);
    }

    @Test
    public void testSimpleWithInnerLessPrioThanGiven() throws MalformedURLException {
        initialClasses(BeanWithInner.class, DummyBean.class);
        configureAndStart();
        assertNotNull(selectGet(BeanWithInner.class).dummyBean);
        assertEquals(selectGet(BeanWithInner.class).dummyBean.getClass(), DummyBean.class);
    }

    @Test
    public void testSimpleWith2InnerLessPrioThanGiven() throws MalformedURLException {
        initialClasses(BeanWith2Inner.class, DummyBean.class);
        configureAndStart();
        assertNotNull(selectGet(BeanWith2Inner.class).dummyBean);
        assertEquals(selectGet(BeanWith2Inner.class).dummyBean.getClass(), DummyBean.class);
    }

    @Test
    public void testSimpleWith2InnerNoOuter() throws MalformedURLException {
        initialClasses(BeanWith2Inner.class);
        configureAndStart();
        assertNotNull(selectGet(BeanWith2Inner.class).dummyBean);
        assertNotEquals(selectGet(BeanWith2Inner.class).dummyBean.getClass(), DummyBean.class);
        assertTrue(DummyBean.class.isAssignableFrom(selectGet(BeanWith2Inner.class).dummyBean.getClass()));
    }

    @Test
    public void testSimpleWithTestResources() throws MalformedURLException {
        initialClasses(Bean.class, TestResources.class);
        configureAndStart();
        assertNotNull(selectGet(Bean.class).dummyBean);
        assertEquals(selectGet(Bean.class).dummyBean.getClass(), DummyBean.class);
    }

    @Test
    public void testSimpleWithTestMocks() throws MalformedURLException {
        initialClasses(Bean.class, TestMocks.class);
        configureAndStart();
        assertNotNull(selectGet(Bean.class).dummyBean);
        assertNotEquals(selectGet(Bean.class).dummyBean.getClass(), DummyBean.class);
        assertTrue(DummyBean.class.isAssignableFrom(selectGet(Bean.class).dummyBean.getClass()));
    }

    @Test
    public void testSimpleWithInnerProducedMock() throws MalformedURLException {
        initialClasses(BeanWithProducedMock.class);
        configureAndStart();
        assertNotNull(selectGet(BeanWithProducedMock.class).dummyBean);
        // is mock
        assertNotEquals(selectGet(BeanWithProducedMock.class).dummyBean.getClass(), DummyBean.class);
        assertTrue(DummyBean.class.isAssignableFrom(selectGet(BeanWithProducedMock.class).dummyBean.getClass()));
    }

    @Test
    public void testSimpleWithInnerProducedMockInPackage() throws MalformedURLException {
        initialClasses(BeanWithProducedMock.class);
        configureAndStart();
        assertNotNull(selectGet(BeanWithProducedMock.class).dummyBean);
        // is mock
        assertNotEquals(selectGet(BeanWithProducedMock.class).dummyBean.getClass(), DummyBean.class);
        assertTrue(DummyBean.class.isAssignableFrom(selectGet(BeanWithProducedMock.class).dummyBean.getClass()));
    }

    @Test
    public void testSimpleWithProducer() throws MalformedURLException {
        initialClasses(BeanWithProducer.class);
        configureAndStart();
        assertNotNull(selectGet(BeanWithProducer.class).dummyBean);
        assertEquals(selectGet(BeanWithProducer.class).dummyBean.getClass(), DummyBean.class);
    }

    static class BeanWithInnerProducer {

        interface DummyInterface {
        }

        static class InnerWithInject extends DummyBean {
            @Mock
            DummyInterface dummyInterfaceMocked;
        }

        @Inject
        DummyInterface dummyBean;
    }

    @Test
    public void canFindProducerInAvailableClass() throws MalformedURLException {
        initialClasses(BeanWithInnerProducer.class);
        configureAndStart();
        assertNotNull(selectGet(BeanWithInnerProducer.class).dummyBean);
        assertNotNull(selectGet(BeanWithInnerProducer.InnerWithInject.class));
        assertTrue(BeanWithInnerProducer.DummyInterface.class.isAssignableFrom(selectGet(BeanWithInnerProducer.class).dummyBean.getClass()));
    }


    static class BeanWithInnerProducerDependent {

        interface DummyInterface {
        }
        static class InnerProducingDummyBean {
            @Mock
            DummyBean dummyBean;
        }
        static class InnerWithInject {
            @Mock
            DummyInterface dummyInterfaceMocked;

            @Inject
            DummyBean dummyBean;
        }

        @Inject
        DummyInterface dummyInterfaceBean;
    }

    @Test
    public void canFindProducerInAvailableClassThatNeedsInject() throws MalformedURLException {
        initialClasses(BeanWithInnerProducerDependent.class);
        configureAndStart();
        assertNotNull(selectGet(BeanWithInnerProducerDependent.class).dummyInterfaceBean);
        assertNotNull(selectGet(BeanWithInnerProducerDependent.InnerWithInject.class));
        assertTrue(BeanWithInnerProducerDependent.DummyInterface.class.isAssignableFrom(selectGet(BeanWithInnerProducerDependent.class).dummyInterfaceBean.getClass()));
    }

    static class BeanUsingAlternative {

        @Alternative
        static class InnerAlternative extends DummyBean {

        }

        @EnabledAlternatives({InnerAlternative.class})
        static class InjectingAlternative {

            @Inject
            DummyBean dummyBean;
        }

        @Inject
        InjectingAlternative injectingAlternative;

    }

    @Test
    public void canInjectAlternativeClass() throws MalformedURLException {
        initialClasses(BeanUsingAlternative.class, DummyBean.class);
        configureAndStart();
        assertNotNull(selectGet(BeanUsingAlternative.class).injectingAlternative);
        assertNotNull(selectGet(BeanUsingAlternative.InjectingAlternative.class).dummyBean);
        assertNotNull(selectGet(BeanUsingAlternative.InnerAlternative.class));
     }

    static class BeanUsingAlternative2 {

        @Alternative
        static class InnerAlternative extends DummyBean {

        }

        static class InjectingAlternative {

            @Inject
            DummyBean dummyBean;
        }

        @Inject
        InjectingAlternative injectingAlternative;

    }

    @Test
    public void canInjectAlternativeClass2() throws MalformedURLException {
        testClass(BeanUsingAlternative2.class);
        enabledAlternatives(BeanUsingAlternative2.InnerAlternative.class);
        configureAndStart();
        assertNotNull(selectGet(BeanUsingAlternative2.class).injectingAlternative);
        assertNotNull(selectGet(BeanUsingAlternative2.InjectingAlternative.class).dummyBean);
        assertNotNull(selectGet(BeanUsingAlternative2.InnerAlternative.class));
        assertEquals(selectGet(DummyBean.class).getClass(), BeanUsingAlternative2.InnerAlternative.class);
    }

    @Test
    public void canInjectAlternativeClass2NotEnabled() throws MalformedURLException {
        testClass(BeanUsingAlternative2.class);
        initialClasses(DummyBean.class);
        configureAndStart();
        assertNotNull(selectGet(BeanUsingAlternative2.class).injectingAlternative);
        assertNotNull(selectGet(BeanUsingAlternative2.InjectingAlternative.class).dummyBean);
        assertNotNull(selectGet(DummyBean.class));
        assertEquals(selectGet(DummyBean.class).getClass(), DummyBean.class);
    }

    static class BeanUsingAlternativeStereotype {

        static class ProducingAlternative {
            @ProducesAlternative
            @Mock
            DummyBean dummyBeanMock;
        }


        static class InjectingAlternative {

            @Inject
            DummyBean dummyBean;
        }

        @Inject
        InjectingAlternative injectingAlternative;

    }

    @Test
    public void canInjectAlternativeStereotypedField() throws MalformedURLException {
        testClass(BeanUsingAlternativeStereotype.class);
        initialClasses(ProducesAlternative.class);
        configureAndStart();
        assertNotNull(selectGet(BeanUsingAlternativeStereotype.class).injectingAlternative);
        assertNotNull(selectGet(BeanUsingAlternativeStereotype.InjectingAlternative.class).dummyBean);
        assertNotNull(selectGet(BeanUsingAlternativeStereotype.ProducingAlternative.class));
        assertNotNull(selectGet(DummyBean.class));
    }

    static class BeanUsingAlternativeAtField {

        static class ProducingAlternative {
            @Alternative
            @Mock
            DummyBean dummyBeanMock;
        }


        static class InjectingAlternative {

            @Inject
            DummyBean dummyBean;
        }

        @Inject
        InjectingAlternative injectingAlternative;

    }

    @Test
    public void canInjectAlternativeField() throws MalformedURLException {
        testClass(BeanUsingAlternativeAtField.class);
        enabledAlternatives(BeanUsingAlternativeAtField.ProducingAlternative.class);
        configureAndStart();
        assertNotNull(selectGet(BeanUsingAlternativeAtField.class).injectingAlternative);
        assertNotNull(selectGet(BeanUsingAlternativeAtField.InjectingAlternative.class).dummyBean);
        assertNotNull(selectGet(BeanUsingAlternativeAtField.ProducingAlternative.class));
        assertNotNull(selectGet(DummyBean.class));
    }

    static class BeanUsingAlternativeAtMethod {

        static class ProducingAlternative {

            static class DummyBeanAlternative extends DummyBean {

                void dummyMethod() {
                    System.out.println("dummy output");
                }
            };

            @Produces
            @Alternative
            DummyBeanAlternative dummyBeanMock() {
                return new DummyBeanAlternative();
            }
        }


        static class InjectingAlternative {
            @Inject
            DummyBean dummyBean;
        }

        @Inject
        InjectingAlternative injectingAlternative;

    }

    @Test
    public void canInjectAlternativeMethod() throws MalformedURLException {
        testClass(BeanUsingAlternativeAtMethod.class);
        enabledAlternatives(BeanUsingAlternativeAtMethod.ProducingAlternative.class);
        configureAndStart();
        assertNotNull(selectGet(BeanUsingAlternativeAtMethod.class).injectingAlternative);
        assertNotNull(selectGet(BeanUsingAlternativeAtMethod.InjectingAlternative.class).dummyBean);
        assertNotNull(selectGet(BeanUsingAlternativeAtMethod.ProducingAlternative.class));
        assertNotNull(selectGet(DummyBean.class));
        assertNotEquals(selectGet(DummyBean.class).getClass(), DummyBean.class);
    }


    private void configureAndStart() throws MalformedURLException {
        CdiConfigBuilder cdiConfigBuilder = new CdiConfigBuilder();
        cdiConfigBuilder.initialize(cfg);
        setBeanClasses(cdiConfigBuilder.toBeStarted());
        setEnabledAlternativeStereotypes(ProducesAlternative.class);
        setAlternativeClasses(cdiConfigBuilder.getEnabledAlternatives());
        setExtensions(cdiConfigBuilder.getExtensions());
        start();
    }


}
