package com.oneandone.ejbcdiunit.asynchmdb;

import static org.junit.Assert.assertThat;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Inject;

import org.hamcrest.CoreMatchers;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.ejbcdiunit.AsynchronousManager;
import com.oneandone.ejbcdiunit.EjbUnitRunner;
import com.oneandone.ejbcdiunit.ejbs.StatelessCompletelyAsynchEJB;

/**
 * @author aschoerk
 */
@RunWith(EjbUnitRunner.class)
@AdditionalClasses({StatelessCompletelyAsynchEJB.class})
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
