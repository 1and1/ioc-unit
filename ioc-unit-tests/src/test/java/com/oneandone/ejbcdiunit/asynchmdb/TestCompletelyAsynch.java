package com.oneandone.ejbcdiunit.asynchmdb;

import static org.junit.Assert.assertThat;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Inject;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.CdiUnit2Runner;
import com.oneandone.iocunit.ejb.AsynchronousManager;
import com.oneandone.ejbcdiunit.ejbs.StatelessCompletelyAsynchEJB;

/**
 * @author aschoerk
 */
@RunWith(CdiUnit2Runner.class)
@SutClasses({ StatelessCompletelyAsynchEJB.class })
public class TestCompletelyAsynch {
    @Inject
    AsynchronousManager asynchronousManager;

    @Inject
    StatelessCompletelyAsynchEJB statelessCompletelyAsynchEJB;

    @Before
    public void beforeTestCompletelyAsynch() {
        asynchronousManager.setEnqueAsynchronousCalls(true);
    }


    @Test
    public void test() throws ExecutionException, InterruptedException {
        Future<Boolean> future = statelessCompletelyAsynchEJB.callAsynch(true);
        assertThat(future.isDone(), CoreMatchers.is(false));
        assertThat(future.isCancelled(), CoreMatchers.is(false));

        asynchronousManager.once();
        assertThat(future.isDone(), CoreMatchers.is(true));
        assertThat(future.get(), CoreMatchers.is(true));
    }


}
