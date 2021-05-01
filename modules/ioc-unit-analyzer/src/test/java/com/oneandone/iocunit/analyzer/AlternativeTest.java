package com.oneandone.iocunit.analyzer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.MalformedURLException;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.oneandone.iocunit.analyzer.annotations.AnalyzerFlags;
import com.oneandone.iocunit.analyzer.annotations.EnabledAlternatives;
import com.oneandone.iocunit.analyzer.annotations.ProducesAlternative;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;

public class AlternativeTest extends BaseTest {

    @AnalyzerFlags(allowGuessing = false, produceInstanceInjectsByAvailables = false)
    @EnabledAlternatives(BeanUsingAlternativeAtField.ProducingAlternative.class)
    static class BeanUsingAlternativeAtField extends BaseClass {

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
        InjectingAlternative injectingAlternative;

    }

    @Test
    public void canInjectAlternativeField() throws MalformedURLException {
        createTest(BeanUsingAlternativeAtField.class);
        assertEquals(3, toBeStarted.size());
        assertArrayEquals(toBeStarted.toArray(), new Class<?>[] {
                BeanUsingAlternativeAtField.class,
                BeanUsingAlternativeAtField.ProducingAlternative.class,
                BeanUsingAlternativeAtField.InjectingAlternative.class});
//        assertNotNull(selectGet(BeanUsingAlternativeAtField.class).injectingAlternative);
//        assertNotNull(selectGet(BeanUsingAlternativeAtField.InjectingAlternative.class).dummyBean);
//        assertNotNull(selectGet(BeanUsingAlternativeAtField.ProducingAlternative.class));
//        assertNotNull(selectGet(DummyBean.class));
    }

    @TestClasses(ProducesAlternative.class)
    static class BeanUsingAlternativeStereotype extends BaseClass {

        static class ProducingAlternative {
            @ProducesAlternative
            @Produces
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
    public void canInjectAlternativeStereotype() throws MalformedURLException {
        createTest(BeanUsingAlternativeStereotype.class);
        assertEquals(4, toBeStarted.size());
        assertArrayEquals(toBeStarted.toArray(), new Class<?>[] {
                BeanUsingAlternativeStereotype.class,
                ProducesAlternative.class,
                BeanUsingAlternativeStereotype.InjectingAlternative.class,
                BeanUsingAlternativeStereotype.ProducingAlternative.class});
    }

    static class DummyBean {

    }

    static class BeanUsingAlternative extends BaseClass {

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

    @org.junit.jupiter.api.Test
    public void canInjectAlternativeClass() throws MalformedURLException {
        createTest(BeanUsingAlternative.class);
        assertEquals(3,toBeStarted.size());
        assertEquals(1, configuration.getEnabledAlternatives().size());
//        assertNotNull(selectGet(BeanUsingAlternative.class).injectingAlternative);
//        assertNotNull(selectGet(BeanUsingAlternative.InjectingAlternative.class).dummyBean);
//        assertNotNull(selectGet(BeanUsingAlternative.InnerAlternative.class));
    }
}
