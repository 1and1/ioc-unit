package com.oneandone.ejbcdiunit5.asynchmdb;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Inject;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.IocJUnit5Extension;
import com.oneandone.iocunit.ejb.AsynchronousManager;
import com.oneandone.ejbcdiunit.ejbs.StatelessCompletelyAsynchEJB;


/**
 * @author aschoerk
 */
@ExtendWith(IocJUnit5Extension.class)
@SutClasses({ StatelessCompletelyAsynchEJB.class })
public class TestCompletelyAsynch {
    @Inject
    AsynchronousManager asynchronousManager;

    @Inject
    StatelessCompletelyAsynchEJB statelessCompletelyAsynchEJB;

    @BeforeEach
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
