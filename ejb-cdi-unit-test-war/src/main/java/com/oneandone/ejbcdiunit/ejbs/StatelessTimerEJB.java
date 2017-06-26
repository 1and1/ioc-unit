package com.oneandone.ejbcdiunit.ejbs;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.Timeout;

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
