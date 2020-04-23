package ejbcdiunit;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunitejb.ejbs.appexc.TestBaseClass;

@RunWith(Arquillian.class)
public class WildflyArquillianAppExcTransactionTest extends TestBaseClass {

    @Deployment
    public static Archive<?> createTestArchive() {
        return WildflyArquillianEjbTransactionTest.getWarFromTargetFolder();
    }

    @Test
    @Override
    public void testDeclaredAppExcInCurrentTra() throws Throwable {
        super.testDeclaredAppExcInCurrentTra();
    }

    @Test
    @Override
    public void testDeclaredAppRtExcInCurrentTra() throws Throwable {
        super.testDeclaredAppRtExcInCurrentTra();
    }

    @Test
    @Override
    public void testAppExcInCurrentTra() throws Throwable {
        super.testAppExcInCurrentTra();
    }

    @Test
    @Override
    public void testAppRTExcInCurrentTra() throws Throwable {
        super.testAppRTExcInCurrentTra();
    }

    @Test
    @Override
    public void testAppExcInRequired() throws Throwable {
        super.testAppExcInRequired();
    }

    @Test
    @Override
    public void testAppRTExcInRequired() throws Throwable {
        super.testAppRTExcInRequired();
    }

    @Test
    @Override
    public void testAppExcInRequiresNew() throws Throwable {
        super.testAppExcInRequiresNew();
    }

    @Test
    @Override
    public void testAppRTExcInRequiresNew() throws Throwable {
        super.testAppRTExcInRequiresNew();
    }

    @Test
    @Override
    public void testAppExcInSupports() throws Throwable {
        super.testAppExcInSupports();
    }

    @Test
    @Override
    public void testAppRTExcInSupports() throws Throwable {
        super.testAppRTExcInSupports();
    }

    @Test
    @Override
    public void testAppExcInNotSupported() throws Throwable {
        super.testAppExcInNotSupported();
    }

    @Test
    @Override
    public void testAppRTExcInNotSupported() throws Throwable {
        super.testAppRTExcInNotSupported();
    }

}