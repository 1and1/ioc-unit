package com.oneandone.ejbcdiunit5.asynchmdb;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.iocunit.analyzer.annotations.ExcludedClasses;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.IocJUnit5Extension;
import com.oneandone.iocunit.ejb.AsynchronousManager;
import com.oneandone.iocunitejb.ejbs.CountingBean;
import com.oneandone.iocunitejb.ejbs.SingletonTimerEJB;
import com.oneandone.iocunitejb.ejbs.StatelessTimerEJB;

/**
 * @author aschoerk
 */
@ExtendWith(IocJUnit5Extension.class)
@SutClasses({StatelessTimerEJB.class })
@ExcludedClasses({ SingletonTimerEJB.class })
public class TestTimerExclusion {
    @Inject
    AsynchronousManager asynchronousManager;


    @Test
    public void testTimer() {
        SingletonTimerEJB a = new SingletonTimerEJB();
        a.logcall();
        assertThat(CountingBean.get(), is(CountingBean.INITIALCOUNT + 1));
        asynchronousManager.once();
        // 1 calls of timer beans
        // inner call of SingletonTimerEJB to StatelessAsynchEJB not yet done
        assertThat(CountingBean.get(), is(CountingBean.INITIALCOUNT + 2));
        asynchronousManager.once();
        // 1 calls of timer beans
        // previous inner call of SingletonTimerEJB to StatelessAsynchEJB added
        assertThat(CountingBean.get(), is(CountingBean.INITIALCOUNT + 3));
    }

}
