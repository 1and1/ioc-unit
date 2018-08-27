package com.oneandone.ejbcdiunit5.asynchmdb;

import com.oneandone.ejbcdiunit.AsynchronousManager;
import com.oneandone.ejbcdiunit.cdiunit.ExcludedClasses;
import com.oneandone.ejbcdiunit.ejbs.CountingBean;
import com.oneandone.ejbcdiunit.ejbs.SingletonTimerEJB;
import com.oneandone.ejbcdiunit.ejbs.StatelessTimerEJB;
import com.oneandone.ejbcdiunit5.JUnit5Extension;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
@AdditionalClasses({ SingletonTimerEJB.class, StatelessTimerEJB.class })
@ExcludedClasses({SingletonTimerEJB.class})
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
