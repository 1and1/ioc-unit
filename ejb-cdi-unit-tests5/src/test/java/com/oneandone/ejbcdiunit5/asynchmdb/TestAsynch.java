package com.oneandone.ejbcdiunit5.asynchmdb;

import com.oneandone.ejbcdiunit.AsynchronousManager;
import com.oneandone.ejbcdiunit.ejbs.*;
import com.oneandone.ejbcdiunit5.JUnit5Extension;
import com.oneandone.ejbcdiunit5.helpers.LoggerGenerator;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

/**
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
@AdditionalClasses({ StatelessAsynchEJB.class, SingletonTimerEJB.class, StatelessTimerEJB.class, LoggerGenerator.class})
public class TestAsynch {

    @Inject
    AsynchronousManager asynchronousManager;

    @Inject
    Logger logger;

    @Inject
    StatelessAsynchEJB statelessAsynchEJB;

    @Inject
    CdiAsynchronousBean cdiAsynchronousBean;

    @BeforeEach
    public void beforeTestAsynch() {
        asynchronousManager.setEnqueAsynchronousCalls(true);
    }

    @Test
    public void test() throws ExecutionException, InterruptedException {
        Future<Boolean> future = statelessAsynchEJB.callAsynch(true);
        assertThat(future.isDone(), is(false));
        assertThat(future.isCancelled(), is(false));

        asynchronousManager.once();
        assertThat(future.isDone(), is(true));
        assertThat(future.get(), is(true));
    }

    @Test
    public void testNotAsyncMethod() throws ExecutionException, InterruptedException {
        assertThat(statelessAsynchEJB.notAsynchronousMethodReturnsOneImmediately(), is(1));

        assertThat(asynchronousManager.thereAreOnces(), is(false));
    }

    @Test
    public void testTimer() {
        SingletonTimerEJB a = new SingletonTimerEJB();
        a.logcall();
        assertThat(CountingBean.get(), is(CountingBean.INITIALCOUNT + 1));
        asynchronousManager.once();
        // 2 calls of timer beans
        // inner call of SingletonTimerEJB to StatelessAsynchEJB not yet done
        assertThat(CountingBean.get(), is(CountingBean.INITIALCOUNT + 3));
        asynchronousManager.once();
        // 2 calls of timer beans
        // previous inner call of SingletonTimerEJB to StatelessAsynchEJB added
        assertThat(CountingBean.get(), is(CountingBean.INITIALCOUNT + 6));
    }

    @Test
    // repeat testTimer to make sure nothing static is left behind
    public void testTimerSecondTest() {
        SingletonTimerEJB a = new SingletonTimerEJB();
        a.logcall();
        assertThat(CountingBean.get(), is(CountingBean.INITIALCOUNT + 1));
        asynchronousManager.once();
        assertThat(CountingBean.get(), is(CountingBean.INITIALCOUNT + 3));
        asynchronousManager.once();
        assertThat(CountingBean.get(), is(CountingBean.INITIALCOUNT + 6));
        asynchronousManager.once();
        assertThat(CountingBean.get(), is(CountingBean.INITIALCOUNT + 9));
    }

    @Test
    public void testTimerWithThread() throws InterruptedException {
        asynchronousManager.startThread();
        SingletonTimerEJB a = new SingletonTimerEJB();
        a.logcall();
        Thread.sleep(2000);
        // 2 calls of timer beans
        // previous inner call of SingletonTimerEJB to StatelessAsynchEJB added
        assertThat(CountingBean.get(), greaterThan(CountingBean.INITIALCOUNT + 6));
        asynchronousManager.stopThread();
        int currentValue = CountingBean.get();
        Thread.sleep(2000);
        assertThat(CountingBean.get(), is(currentValue));
    }

}
