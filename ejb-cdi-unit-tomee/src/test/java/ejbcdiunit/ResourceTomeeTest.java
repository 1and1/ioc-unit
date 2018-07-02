package ejbcdiunit;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;
import javax.ejb.EJB;

import org.apache.openejb.config.EjbModule;
import org.apache.openejb.jee.Beans;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.StatelessBean;
import org.apache.openejb.junit.ApplicationComposerRule;
import org.apache.openejb.testing.Module;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.ejbcdiunit.ejbs.ResourceTestEjb;

/**
 * @author aschoerk
 */
public class ResourceTomeeTest {
    @Rule
    public ApplicationComposerRule applicationComposerRule = new ApplicationComposerRule(this);

    Logger log = LoggerFactory.getLogger("ResourceTomeeTest");

    @EJB
    ResourceTestEjb resourceTestEjb;

    @Module
    public EjbModule module() {
        EjbModule module = new EjbModule(new EjbJar("test-beans")
                .enterpriseBean(new StatelessBean(ResourceTestEjb.class))
        );
        Beans beans = new Beans();
        module.setBeans(beans);
        module.setModuleId("test-module");
        return module;
    }

    @Test
    public void resourceAppNameIsInjected() {
        assertEquals("ResourceTomeeTest",resourceTestEjb.ejbAppName());
    }

    @Test
    public void resourceModuleNameIsInjected() {
        assertEquals("test-module", resourceTestEjb.ejbModuleName());
    }


    @Resource(lookup = "java:app/AppName")
    private String appName;

    @Resource(lookup = "java:module/ModuleName")
    private String moduleName;


    @Test
    public void resourceAppNameIsInjectedInTest() {
        assertEquals("ResourceTomeeTest", appName);
    }

    @Test
    public void resourceModuleNameIsInjectedInTest() {
        assertTrue(moduleName.startsWith("EjbModule"));
    }


}
