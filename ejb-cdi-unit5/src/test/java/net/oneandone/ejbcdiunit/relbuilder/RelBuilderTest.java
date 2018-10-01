package net.oneandone.ejbcdiunit.relbuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder;

import net.oneandone.ejbcdiunit.relbuilder.beans.BeanToBeInjected;
import net.oneandone.ejbcdiunit.relbuilder.beans.ConstructedTestBean;
import net.oneandone.ejbcdiunit.relbuilder.beans.TestBeanWithInjectedField;
import net.oneandone.ejbcdiunit.relbuilder.beans.TestBeanWithProducerInject;

/**
 * @author aschoerk
 */
public class RelBuilderTest {

    @Test
    public void canInjectOneField() throws CdiRelBuilder.AnalyzerException {
        CdiRelBuilder x = new CdiRelBuilder(Arrays.asList(TestBeanWithInjectedField.class));

        CdiRelBuilder.RootRel root = x.getRootRel();
        Map<Class<?>, CdiRelBuilder.Intermediate> beanClasses = root.getBeanClasses();

        assertNotNull(beanClasses.get(TestBeanWithInjectedField.class));
        assertNotNull(beanClasses.get(BeanToBeInjected.class));
        assertEquals(2, beanClasses.size());

    }

    @Test
    public void canInjectViaConstructor() throws CdiRelBuilder.AnalyzerException {
        CdiRelBuilder x = new CdiRelBuilder(Arrays.asList(ConstructedTestBean.class));

        CdiRelBuilder.RootRel root = x.getRootRel();
        Map<Class<?>, CdiRelBuilder.Intermediate> beanClasses = root.getBeanClasses();

        assertNotNull(beanClasses.get(ConstructedTestBean.class));
        assertNotNull(beanClasses.get(BeanToBeInjected.class));
        assertEquals(2, beanClasses.size());
    }

    @Test
    public void canInjectOneProducerParameter() throws CdiRelBuilder.AnalyzerException {
        CdiRelBuilder x = new CdiRelBuilder(Arrays.asList(TestBeanWithProducerInject.class));

        CdiRelBuilder.RootRel root = x.getRootRel();
        Map<Class<?>, CdiRelBuilder.Intermediate> beanClasses = root.getBeanClasses();

        assertNotNull(beanClasses.get(TestBeanWithProducerInject.class));
        assertNotNull(beanClasses.get(BeanToBeInjected.class));
        assertEquals(2, beanClasses.size());
    }
}
