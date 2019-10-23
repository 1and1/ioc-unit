package ejbcdiunit;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.iocunitejb.ejbs.ResourceTestEjb;
import com.oneandone.iocunitejb.ejbs.SingletonEJB;
import com.oneandone.iocunitejb.ejbs.StatelessAsynchEJB;
import com.oneandone.iocunitejb.ejbs.StatelessEJB;

/**
 * @author aschoerk
 */
@RunWith(Arquillian.class)
public class WildflyArquillianResourceTest {

    @Deployment
    public static Archive<?> createTestArchive() {
        return WildflyArquillianEjbTransactionTest.getWarFromTargetFolder();
    }

    Logger log = LoggerFactory.getLogger("WildflyArquillianResourceTest");

    @EJB
    ResourceTestEjb resourceTestEjb;


    @Resource(lookup = "java:app/AppName")
    private String appName;

    @Resource(lookup = "java:module/ModuleName")
    private String moduleName;

    // @Inject
    // QMdbEjb qMdbEjb;

    @Inject
    StatelessAsynchEJB statelessAsynchEJB;

    @Inject
    StatelessEJB statelessEJB;

    @Inject
    SingletonEJB singletonEJB;

    @Test
    public void checkContextTypes() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        assertNotNull(statelessAsynchEJB.getEjbContext());
        assertNotNull(statelessEJB.getEjbContext());
        Assert.assertTrue(statelessAsynchEJB.getEjbContext() instanceof SessionContext);
        Assert.assertTrue(statelessEJB.getEjbContext() instanceof SessionContext);
    }


    @Test
    public void resourceAppNameIsInjectedInBean() {
        assertTrue(resourceTestEjb.ejbAppName().contains("ioc-unit-test-war"));
    }

    @Test
    public void resourceModuleNameIsInjectedBean() {
        assertTrue(resourceTestEjb.ejbAppName().contains("ioc-unit-test-war"));
    }

    @Test
    public void resourceAppNameIsInjectedInTest() {
        assertTrue(appName.contains("ioc-unit-test-war"));
        // new InitialContext().list("")
        // assertEquals("", appNameByName);
    }


}
