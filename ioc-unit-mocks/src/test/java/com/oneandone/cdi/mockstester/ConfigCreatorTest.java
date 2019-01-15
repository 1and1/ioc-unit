package com.oneandone.cdi.mockstester;

import com.oneandone.iocunit.analyzer.annotations.EnabledAlternatives;
import com.oneandone.iocunit.analyzer.annotations.ProducesAlternative;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.annotations.SutPackages;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author aschoerk
 */
public class ConfigCreatorTest extends TestsBase {




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
        @Produces
        @Mock // , extension not available yet
                DummyBean dummyBeanMock; // = Mockito.mock(DummyBean.class);
    }


    @SutClasses(TestResources.class)
    static class BeanWithProducer {
        @Inject
        private DummyBean dummyBean;
    }


    @SutClasses({TestMocks.class})
    static class BeanWithProducedMock {
        @Inject
        private DummyBean dummyBean;
    }

    @SutPackages({TestMocks.class})
    static class BeanWithSutPackages {
        @Inject
        private DummyBean dummyBean;
    }


    static class BeanWith2Inner {

        static class InnerDummy extends DummyBean {

        }

        static class InnerDummy2 extends DummyBean {

        }

        @Inject
        private DummyBean dummyBean;
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
        private DummyBean dummyBean;

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
            @Produces
            @Mock
            DummyInterface dummyInterfaceMocked;
        }

        @Inject
        private DummyInterface dummyBean;

        public DummyInterface getDummyBean() {
            return dummyBean;
        }
    }

    @Test
    public void canFindProducerInAvailableClass() throws MalformedURLException {
        testClass(BeanWithInnerProducer.class);
        configureAndStart();
        assertNotNull(selectGet(BeanWithInnerProducer.class).getDummyBean());
        assertNotNull(selectGet(BeanWithInnerProducer.InnerWithInject.class));
        assertTrue(BeanWithInnerProducer.DummyInterface.class.isAssignableFrom(selectGet(BeanWithInnerProducer.class)
                .getDummyBean().getClass()));
    }


    static class BeanWithInnerProducerDependent {

        interface DummyInterface {
        }

        static class InnerProducingDummyBean {
            @Produces
            @Mock
            DummyBean dummyBean;
        }

        static class InnerWithInject {
            @Produces
            @Mock
            DummyInterface dummyInterfaceMocked;

            @Inject
            DummyBean dummyBean;
        }

        @Inject
        private DummyInterface dummyInterfaceBean;
    }

    @Test
    public void canFindProducerInAvailableClassThatNeedsInject() throws MalformedURLException {
        initialClasses(BeanWithInnerProducerDependent.class);
        configureAndStart();
        assertNotNull(selectGet(BeanWithInnerProducerDependent.class).dummyInterfaceBean);
        assertNotNull(selectGet(BeanWithInnerProducerDependent.InnerWithInject.class));
        assertTrue(BeanWithInnerProducerDependent.DummyInterface.class
                .isAssignableFrom(selectGet(BeanWithInnerProducerDependent.class).dummyInterfaceBean.getClass()));
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
        private InjectingAlternative injectingAlternative;


    }

    @Test
    public void canInjectAlternativeClass() throws MalformedURLException {
        initialClasses(BeanUsingAlternative.class,
                // BeanUsingAlternative.InjectingAlternative.class,
                DummyBean.class);
        configureAndStart();
        assertNotNull(selectGet(BeanUsingAlternative.class).injectingAlternative);
        assertNotNull(selectGet(BeanUsingAlternative.InjectingAlternative.class).dummyBean);
        assertNotNull(selectGet(BeanUsingAlternative.InnerAlternative.class));
    }


    static class BeanUsingAlternativeStereotype {

        static class ProducingAlternative {
            @ProducesAlternative
            @Produces
            DummyBean dummyBeanMock = new DummyBean();
        }


        static class InjectingAlternative {
            @Inject
            DummyBean dummyBean;
        }

        @Inject
        private InjectingAlternative injectingAlternative;

        public InjectingAlternative getInjectingAlternative() {
            return injectingAlternative;
        }
    }

    @Test
    public void canInjectAlternativeStereotypedField() throws MalformedURLException {
        testClass(BeanUsingAlternativeStereotype.class);
        initialClasses(ProducesAlternative.class, BeanUsingAlternativeStereotype.ProducingAlternative.class);
        configureAndStart();
        assertNotNull(selectGet(BeanUsingAlternativeStereotype.class).getInjectingAlternative());
        assertNotNull(selectGet(BeanUsingAlternativeStereotype.InjectingAlternative.class).dummyBean);
        assertNotNull(selectGet(BeanUsingAlternativeStereotype.ProducingAlternative.class));
        assertNotNull(selectGet(DummyBean.class));
    }

    static class BeanUsingAlternativeAtField {

        @Alternative
        static class ProducingAlternative {
            @Produces
            @Mock
            DummyBean dummyBeanMock;
        }


        static class InjectingAlternative {

            @Inject
            DummyBean dummyBean;
        }

        @Inject
        private InjectingAlternative injectingAlternative;

        public InjectingAlternative getInjectingAlternative() {
            return injectingAlternative;
        }
    }

    @Test
    public void canInjectAlternativeField() throws MalformedURLException {
        testClass(BeanUsingAlternativeAtField.class);
        enabledAlternatives(BeanUsingAlternativeAtField.ProducingAlternative.class);
        configureAndStart();
        assertNotNull(selectGet(BeanUsingAlternativeAtField.class).getInjectingAlternative());
        assertNotNull(selectGet(BeanUsingAlternativeAtField.InjectingAlternative.class).dummyBean);
        assertNotNull(selectGet(BeanUsingAlternativeAtField.ProducingAlternative.class));
        assertNotNull(selectGet(DummyBean.class));
    }

    static class BeanUsingAlternativeAtMethod {

        @Alternative
        static class ProducingAlternative {

            static class DummyBeanAlternative extends DummyBean {

                void dummyMethod() {
                    System.out.println("dummy output");
                }
            }

            @Produces
            DummyBeanAlternative dummyBeanMock() {
                return new DummyBeanAlternative();
            }
        }


        static class InjectingAlternative {
            @Inject
            DummyBean dummyBean;
        }

        @Inject
        private  InjectingAlternative injectingAlternative;

        public InjectingAlternative getInjectingAlternative() {
            return injectingAlternative;
        }
    }

    @Test
    public void canInjectAlternativeMethod() throws MalformedURLException {
        testClass(BeanUsingAlternativeAtMethod.class);
        enabledAlternatives(BeanUsingAlternativeAtMethod.ProducingAlternative.class);
        configureAndStart();
        assertNotNull(selectGet(BeanUsingAlternativeAtMethod.class).getInjectingAlternative());
        assertNotNull(selectGet(BeanUsingAlternativeAtMethod.InjectingAlternative.class).dummyBean);
        assertNotNull(selectGet(BeanUsingAlternativeAtMethod.ProducingAlternative.class));
        assertNotNull(selectGet(DummyBean.class));
        assertNotEquals(selectGet(DummyBean.class).getClass(), DummyBean.class);
    }




}
