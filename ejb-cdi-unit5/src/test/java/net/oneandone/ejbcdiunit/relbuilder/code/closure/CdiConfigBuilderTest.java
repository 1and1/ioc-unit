package net.oneandone.ejbcdiunit.relbuilder.code.closure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.util.Arrays;

/**
 * @author aschoerk
 */
public class CdiConfigBuilderTest {

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

    @Nested
    class SimpleTests extends Base {

        @Test
        public void testSimple1() throws MalformedURLException {
            initialClasses(DummyBean.class, Bean.class);
            CdiConfigBuilder cdiConfigBuilder = new CdiConfigBuilder();
            cdiConfigBuilder.initialize(cfg);
        }

        @Test
        public void testSimplePart() throws MalformedURLException {
            initialClasses(Bean.class);
            CdiConfigBuilder cdiConfigBuilder = new CdiConfigBuilder();
            cdiConfigBuilder.initialize(cfg);
        }
    }




}
