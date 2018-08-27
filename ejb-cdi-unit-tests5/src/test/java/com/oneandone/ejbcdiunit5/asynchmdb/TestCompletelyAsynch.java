package com.oneandone.ejbcdiunit5.asynchmdb;

import com.oneandone.ejbcdiunit.AsynchronousManager;
import com.oneandone.ejbcdiunit.ejbs.StatelessCompletelyAsynchEJB;
import com.oneandone.ejbcdiunit5.JUnit5Extension;
import org.hamcrest.CoreMatchers;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.hamcrest.MatcherAssert.assertThat;


/**
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
@AdditionalClasses({StatelessCompletelyAsynchEJB.class})
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
