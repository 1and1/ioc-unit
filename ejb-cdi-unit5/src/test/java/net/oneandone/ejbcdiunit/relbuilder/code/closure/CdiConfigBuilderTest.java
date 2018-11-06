package net.oneandone.ejbcdiunit.relbuilder.code.closure;

import java.util.Arrays;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    public void test() {
        initialClasses(DummyBean.class, Bean.class);


    }


}
