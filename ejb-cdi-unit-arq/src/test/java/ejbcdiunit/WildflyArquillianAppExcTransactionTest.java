package ejbcdiunit;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.ejbcdiunit.ejbs.appexc.TestBaseClass;

@RunWith(Arquillian.class)
public class WildflyArquillianAppExcTransactionTest extends TestBaseClass {

    public static WebArchive getWarFromTargetFolder() {
        File folder = new File("../ejb-cdi-unit-test-war/target/");
        File[] files = folder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".war");
            }
        });
        if (files == null) {
            throw new IllegalArgumentException("Could not find directory " + folder.toString());
        } else if (files.length != 1) {
            throw new IllegalArgumentException("Exactly 1 war file expected, but found " + Arrays.toString(files));
        } else {
            WebArchive war = (WebArchive) ShrinkWrap.createFromZipFile(WebArchive.class, files[0]);
            return war;
        }
    }

    @Deployment
    public static Archive<?> createTestArchive() {
        return getWarFromTargetFolder();
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