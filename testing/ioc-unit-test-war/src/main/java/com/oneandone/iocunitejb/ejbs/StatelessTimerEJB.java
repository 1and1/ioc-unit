package com.oneandone.iocunitejb.ejbs;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.ejb.Timeout;

/**
 * @author aschoerk
 */
@Stateless
public class StatelessTimerEJB extends CountingBean {

    @PostConstruct
    public void postConstruct() {
        setPostConstructCalled();
    }

    @Timeout
    public void callAsynch()  {
        if (isPostConstructCalled()) {
            logcall();
            return;
        } else {
            logger.error("postconstruct did not work for this instance");
        }
    }
}
