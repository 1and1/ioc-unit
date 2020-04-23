package com.oneandone.iocunitejb.asynchmdb;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import com.oneandone.iocunit.analyzer.annotations.ExcludedClasses;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import org.junit.Rule;
import org.junit.Test;

import com.oneandone.iocunit.analyzer.InitialConfiguration;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.IocUnitRule;
import com.oneandone.iocunit.ejb.AsynchronousManager;
import com.oneandone.iocunitejb.ejbs.CountingBean;
import com.oneandone.iocunitejb.ejbs.SingletonTimerEJB;
import com.oneandone.iocunitejb.ejbs.StatelessTimerEJB;

/**
 * @author aschoerk
 */
@SutClasses({ StatelessTimerEJB.class, SingletonTimerEJB.class })
public class TestTimerExclusion {
    @Inject
    AsynchronousManager asynchronousManager;

    @Rule
    public IocUnitRule getEjbUnitRule() {
        return new IocUnitRule(this, new InitialConfiguration().exclude(SingletonTimerEJB.class));
    }

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
