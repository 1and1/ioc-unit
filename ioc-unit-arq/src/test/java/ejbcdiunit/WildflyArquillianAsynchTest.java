package ejbcdiunit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.ejbcdiunit.ejbs.CdiAsynchronousBean;
import com.oneandone.ejbcdiunit.ejbs.StatelessAsynchEJB;

/**
 * @author aschoerk
 */
@RunWith(Arquillian.class)
public class WildflyArquillianAsynchTest {
    Logger logger = LoggerFactory.getLogger("AsynchTest");
    @Inject
    StatelessAsynchEJB statelessAsynchEJB;
    @Inject
    CdiAsynchronousBean cdiAsynchronousBean;

    @Deployment
    public static Archive<?> createTestArchive() {
        return WildflyArquillianTransactionTest.createTestArchive();
    }

    @Test
    public void testAsynchStateless() throws ExecutionException, InterruptedException {
        Future<Boolean> result = statelessAsynchEJB.callAsynch(true);
        assertTrue( result.get());
        result = statelessAsynchEJB.callAsynch(false);
        assertFalse( result.get());
    }

    @Test
    public void testAsynchStatelessWithWait() throws ExecutionException, InterruptedException {
        long started = System.currentTimeMillis();
        Future<Boolean> result = statelessAsynchEJB.callAsynchSleep1000(true);
        if (result.isDone()) {
            logger.error("testAsynchStatelessWithWait: result is already done, possibly not really asynchronous.");
            assertTrue(System.currentTimeMillis() > started + 999);
        } else {
            logger.info("testAsynchStatelessWithWait: result is not done yet, seems good");
        }
        assertTrue( result.get());
        result = statelessAsynchEJB.callAsynchSleep1000(false);
        assertFalse( result.get());
        started = System.currentTimeMillis();
        List<Future<Boolean>> results = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            results.add(statelessAsynchEJB.callAsynchSleep1000(i % 1 == 1));
        }
        assertTrue("testAsynchStatelessWithWait: lasted too long, might not be asynchronous",System.currentTimeMillis() < started + 10000);
    }

    @Test
    public void testCdiAsyncWithWait() throws ExecutionException, InterruptedException {
        long started = System.currentTimeMillis();
        Future<Boolean> result = cdiAsynchronousBean.callAsynchSleep1000(true);
        if (result.isDone()) {
            logger.error("testCdiAsyncWithWait: result is already done, possibly not really asynchronous.");
            assertTrue(System.currentTimeMillis() > started + 999);
        } else {
            logger.info("testCdiAsyncWithWait: result is already done");
        }

        assertTrue(result.get());
        started = System.currentTimeMillis();
        result = cdiAsynchronousBean.callAsynchSleep1000(false);
        assertFalse( result.get());
        assertTrue(System.currentTimeMillis() > started + 999);

        started = System.currentTimeMillis();
        List<Future<Boolean>> results = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            results.add(cdiAsynchronousBean.callAsynchSleep1000(i % 1 == 1));
        }
        assertTrue("testCdiAsyncWithWait: lasted not long enough, might be asynchronous in spite of pure CDI bean.",System.currentTimeMillis() > started + 10000);

    }
}
