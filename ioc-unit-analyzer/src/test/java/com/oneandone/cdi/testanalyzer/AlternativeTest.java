package com.oneandone.cdi.testanalyzer;

import com.oneandone.cdi.testanalyzer.annotations.EnabledAlternatives;

import com.oneandone.cdi.testanalyzer.annotations.ProducesAlternative;
import com.oneandone.cdi.testanalyzer.annotations.TestClasses;
import org.junit.Test;
import org.mockito.Mock;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.net.MalformedURLException;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AlternativeTest extends BaseTest {

    @EnabledAlternatives(BeanUsingAlternativeAtField.ProducingAlternative.class)
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
        InjectingAlternative injectingAlternative;

    }

    @Test
    public void canInjectAlternativeField() throws MalformedURLException {
        createTest(BeanUsingAlternativeAtField.class);
        assertEquals(3, toBeStarted.size());
//        assertNotNull(selectGet(BeanUsingAlternativeAtField.class).injectingAlternative);
//        assertNotNull(selectGet(BeanUsingAlternativeAtField.InjectingAlternative.class).dummyBean);
//        assertNotNull(selectGet(BeanUsingAlternativeAtField.ProducingAlternative.class));
//        assertNotNull(selectGet(DummyBean.class));
    }

    @TestClasses(ProducesAlternative.class)
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
    public void canInjectAlternativeStereotype() throws MalformedURLException {
        createTest(BeanUsingAlternativeStereotype.class);
        assertEquals(3, toBeStarted.size());
//        assertNotNull(selectGet(BeanUsingAlternativeAtField.class).injectingAlternative);
//        assertNotNull(selectGet(BeanUsingAlternativeAtField.InjectingAlternative.class).dummyBean);
//        assertNotNull(selectGet(BeanUsingAlternativeAtField.ProducingAlternative.class));
//        assertNotNull(selectGet(DummyBean.class));
    }

    static class DummyBean {

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

    @org.junit.Test
    public void canInjectAlternativeClass() throws MalformedURLException {
        createTest(BeanUsingAlternative.class);
        assertEquals(3,toBeStarted.size());
        assertEquals(1, configuration.getEnabledAlternatives().size());
//        assertNotNull(selectGet(BeanUsingAlternative.class).injectingAlternative);
//        assertNotNull(selectGet(BeanUsingAlternative.InjectingAlternative.class).dummyBean);
//        assertNotNull(selectGet(BeanUsingAlternative.InnerAlternative.class));
    }
}
