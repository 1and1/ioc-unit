package ejbcdiunit;

import static junit.framework.TestCase.assertTrue;

import javax.annotation.Resource;
import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.ejbcdiunit.ejbs.ResourceTestEjb;

/**
 * @author aschoerk
 */
@RunWith(Arquillian.class)
public class WildflyArquillianResourceTest {

    @Deployment
    public static Archive<?> createTestArchive() {
        return WildflyArquillianTransactionTest.getWarFromTargetFolder();
    }

    Logger log = LoggerFactory.getLogger("WildflyArquillianResourceTest");

    @EJB
    ResourceTestEjb resourceTestEjb;


    @Resource(lookup = "java:app/AppName")
    private String appName;

    @Resource(lookup = "java:module/ModuleName")
    private String moduleName;



    @Test
    public void resourceAppNameIsInjectedInBean() {
        assertTrue(resourceTestEjb.ejbAppName().contains("ejb-cdi-unit-test-war"));
    }

    @Test
    public void resourceModuleNameIsInjectedBean() {
        assertTrue(resourceTestEjb.ejbAppName().contains("ejb-cdi-unit-test-war"));
    }

    @Test
    public void resourceAppNameIsInjectedInTest() {
        assertTrue(appName.contains("ejb-cdi-unit-test-war"));
        // new InitialContext().list("")
        // assertEquals("", appNameByName);
    }


}
