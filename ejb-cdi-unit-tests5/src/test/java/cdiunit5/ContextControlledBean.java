package cdiunit5;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jglue.cdiunit.InRequestScope;
import org.junit.jupiter.api.Assertions;

import com.oneandone.ejbcdiunit.ContextControllerEjbCdiUnit;

/**
 * @author aschoerk
 */
public class ContextControlledBean {
    private static final AtomicInteger counter = new AtomicInteger(1);

    @Inject
    private ContextControllerEjbCdiUnit contextController;

    @Inject
    private ContextControlledBean.TestCounter testCounter1;

    @Inject
    private ContextControlledBean.TestCounter testCounter2;

    @Inject
    private ContextControlledBean.TestCallable testCallable;


    @InRequestScope
    public void testSynchronousExecution() {
        Assertions.assertEquals(testCounter1.getTestCounter(), testCounter2.getTestCounter(), "Counter values should be equal.");
    }

    @InRequestScope
    public void testAsynchronousExecution() throws ExecutionException, InterruptedException {

        Assertions.assertEquals(testCounter1.getTestCounter(), testCounter2.getTestCounter(), "Counter values should be equal.");

        Future<Integer> testCallableResult = Executors.newSingleThreadExecutor().submit(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                try {
                    contextController.openRequest();
                    return testCallable.call();
                } finally {
                    contextController.closeRequest();
                }
            }
        });

        Assertions.assertTrue((testCallableResult.get() != testCounter1.getTestCounter()), "Counter values should not be equal.");
    }

    @Produces
    @RequestScoped
    TestCounter createTestCounter() {
        return new TestCounter(counter.getAndIncrement());
    }

    public static class TestCallable implements Callable<Integer> {

        @Inject
        private TestCounter testCounter;

        @Override
        public Integer call() throws Exception {
            return testCounter.getTestCounter();
        }
    }

    @Alternative
    public static class TestCounter {

        private int testCounter;

        public TestCounter() {
            // To make it proxyable
        }

        public TestCounter(int counter) {
            this.testCounter = counter;
        }

        public int getTestCounter() {
            return testCounter;
        }
    }


}
