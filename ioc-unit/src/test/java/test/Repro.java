package test;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author aschoerk
 */
@Ignore
@RunWith(JUnit4.class)
public class Repro {

    @Test
    public void repro1() {
        Weld weld = new Weld()
                .disableDiscovery()
                .addBeanClass(SubExcludeTest.class)
                .addBeanClass(SutBean.class);
        WeldContainer container = weld.initialize();
    }

}
