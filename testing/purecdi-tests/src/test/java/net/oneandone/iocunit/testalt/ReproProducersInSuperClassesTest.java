package net.oneandone.iocunit.testalt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.util.reflection.Formats;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class ReproProducersInSuperClassesTest {
    public String getVersion() {
        return Formats.version();
    }

    @Test
    public void repro1() {
        if(getVersion().startsWith("2")) {
            throw new RuntimeException("does not work in Weld 2");
        }
        Weld weld = new Weld()
                .disableDiscovery()
                .addBeanClass(MainClass.class)
                .addBeanClass(BeanContainer.class)
                .addBeanClass(BeanContainerAlt.class)
                .alternatives(BeanContainerAlt.class);
        try (WeldContainer container = weld.initialize()) {
            final MainClass mainClass = container.select(MainClass.class).get();
            final BeanContainer containerObject = container.select(BeanContainer.class).get();
            assertEquals(BeanContainerAlt.class, containerObject.getClass());
            assertFalse(mainClass.bean.didPostConstruct);
            assertEquals(10, (long) mainClass.producedInt);
        }
    }

    @Test(expected = RuntimeException.class)
    public void reproDeploymentException() {
        if(getVersion().startsWith("2")) {
            throw new RuntimeException("does not work in Weld 2");
        }
        Weld weld = new Weld()
                .disableDiscovery()
                .addBeanClass(MainClass.class)
                .addBeanClass(Bean.class)
                .addBeanClass(BeanContainer.class)
                .addBeanClass(BeanContainerAlt.class)
                .alternatives(BeanContainerAlt.class);
        try (WeldContainer container = weld.initialize()) {
            final MainClass mainClass = container.select(MainClass.class).get();
            assertFalse(mainClass.bean.didPostConstruct);
            assertEquals(10, (long) mainClass.producedInt);
        }
    }
}
