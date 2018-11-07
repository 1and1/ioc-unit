package net.oneandone.ejbcdiunit.relbuilder.code.closure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.util.Arrays;

/**
 * @author aschoerk
 */
public class CdiConfigBuilderTest {

    InitialConfiguration cfg = new InitialConfiguration();

    public void initialClasses(Class<?>... classes) {
        cfg.initialClasses.addAll(Arrays.asList(classes));
    }

    public void testClass(Class clazz) {
        cfg.testClass = clazz;
    }

    @BeforeEach
    public void beforeEach() {
        cfg = new InitialConfiguration();
    }


    static class DummyBean {

    }

    static class Bean {
        @Inject
        DummyBean dummyBean;
    }

    @Test
    public void test() throws MalformedURLException {
        initialClasses(DummyBean.class, Bean.class);
        CdiConfigBuilder cdiConfigBuilder = new CdiConfigBuilder();
        cdiConfigBuilder.initialize(cfg);


    }


}
