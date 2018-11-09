package com.oneandone.ejbcdiunit5.closure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.MalformedURLException;
import java.util.Arrays;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.oneandone.ejbcdiunit.closure.CdiConfigBuilder;
import com.oneandone.ejbcdiunit.closure.InitialConfiguration;
import com.oneandone.ejbcdiunit.closure.annotations.SutClasses;
import com.oneandone.ejbcdiunit.closure.annotations.SutPackages;
import com.oneandone.ejbcdiunit.weldstarter.WeldStarterTestBase;

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


    static class BeanWithInner {
        static class InnerDummy extends DummyBean {

        }

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

    @Nested
    class SimpleTests {

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
    }

    private void configureAndStart() throws MalformedURLException {
        CdiConfigBuilder cdiConfigBuilder = new CdiConfigBuilder();
        cdiConfigBuilder.initialize(cfg);
        setBeanClasses(cdiConfigBuilder.toBeStarted());
        setExtensions(cdiConfigBuilder.getExtensions());
        start();
    }


}
