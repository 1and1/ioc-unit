package com.oneandone.ejbcdiunit.asynchmdb;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalClasses;
import org.junit.Rule;
import org.junit.Test;

import com.oneandone.ejbcdiunit.AsynchronousManager;
import com.oneandone.ejbcdiunit.CdiTestConfig;
import com.oneandone.ejbcdiunit.EjbUnitRule;
import com.oneandone.ejbcdiunit.ejbs.CountingBean;
import com.oneandone.ejbcdiunit.ejbs.SingletonTimerEJB;
import com.oneandone.ejbcdiunit.ejbs.StatelessTimerEJB;

/**
 * @author aschoerk
 */
@AdditionalClasses({ SingletonTimerEJB.class, StatelessTimerEJB.class })
public class TestTimerExclusion {
    @Inject
    AsynchronousManager asynchronousManager;

    @Rule
    public EjbUnitRule getEjbUnitRule() {
        return new EjbUnitRule(this, new CdiTestConfig().addExcluded(SingletonTimerEJB.class));
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
