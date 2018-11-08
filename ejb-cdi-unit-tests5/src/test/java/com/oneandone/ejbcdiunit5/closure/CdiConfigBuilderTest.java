package com.oneandone.ejbcdiunit5.closure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.MalformedURLException;
import java.util.Arrays;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.oneandone.ejbcdiunit.closure.CdiConfigBuilder;
import com.oneandone.ejbcdiunit.closure.InitialConfiguration;
import com.oneandone.ejbcdiunit.weldstarter.WeldStarterTestBase;

/**
 * @author aschoerk
 */
public class CdiConfigBuilderTest extends WeldStarterTestBase {


    @AfterEach
    public void afterEach() {
        tearDown();
    }


    static class Base {
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

    }

    static class DummyBean {

    }

    static class Bean {
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

    @Nested
    class SimpleTests extends Base {

        @Test
        public void testSimple1() throws MalformedURLException {
            initialClasses(DummyBean.class, Bean.class);
            CdiConfigBuilder cdiConfigBuilder = new CdiConfigBuilder();
            cdiConfigBuilder.initialize(cfg);
            setBeanClasses(cdiConfigBuilder.toBeStarted());
            start();
            assertNotNull(selectGet(Bean.class).dummyBean);
        }

        @Test
        public void testSimplePart() throws MalformedURLException {
            Assertions.assertThrows(RuntimeException.class, () -> {
                initialClasses(Bean.class);
                CdiConfigBuilder cdiConfigBuilder = new CdiConfigBuilder();
                cdiConfigBuilder.initialize(cfg);
                setBeanClasses(cdiConfigBuilder.toBeStarted());
                start();
            });
        }

        @Test
        public void testSimpleWithInner() throws MalformedURLException {
            initialClasses(BeanWithInner.class);
            CdiConfigBuilder cdiConfigBuilder = new CdiConfigBuilder();
            cdiConfigBuilder.initialize(cfg);
            setBeanClasses(cdiConfigBuilder.toBeStarted());
            start();
            assertNotNull(selectGet(BeanWithInner.class).dummyBean);
            assertEquals(selectGet(BeanWithInner.class).dummyBean.getClass(), BeanWithInner.InnerDummy.class);
        }

        @Test
        public void testSimpleWithInnerLessPrioThanGiven() throws MalformedURLException {
            initialClasses(BeanWithInner.class, DummyBean.class);
            CdiConfigBuilder cdiConfigBuilder = new CdiConfigBuilder();
            cdiConfigBuilder.initialize(cfg);
            setBeanClasses(cdiConfigBuilder.toBeStarted());
            start();
            assertNotNull(selectGet(BeanWithInner.class).dummyBean);
            assertEquals(selectGet(BeanWithInner.class).dummyBean.getClass(), DummyBean.class);
        }

        @Test
        public void testSimpleWith2InnerLessPrioThanGiven() throws MalformedURLException {
            initialClasses(BeanWith2Inner.class, DummyBean.class);
            CdiConfigBuilder cdiConfigBuilder = new CdiConfigBuilder();
            cdiConfigBuilder.initialize(cfg);
            setBeanClasses(cdiConfigBuilder.toBeStarted());
            start();
            assertNotNull(selectGet(BeanWith2Inner.class).dummyBean);
            assertEquals(selectGet(BeanWith2Inner.class).dummyBean.getClass(), DummyBean.class);
        }

        @Test
        public void testSimpleWith2InnerNoOuter() throws MalformedURLException {
            initialClasses(BeanWith2Inner.class);
            CdiConfigBuilder cdiConfigBuilder = new CdiConfigBuilder();
            cdiConfigBuilder.initialize(cfg);
            setBeanClasses(cdiConfigBuilder.toBeStarted());
            start();
            assertNotNull(selectGet(BeanWith2Inner.class).dummyBean);
            assertNotEquals(selectGet(BeanWith2Inner.class).dummyBean.getClass(), DummyBean.class);
        }

    }


}
