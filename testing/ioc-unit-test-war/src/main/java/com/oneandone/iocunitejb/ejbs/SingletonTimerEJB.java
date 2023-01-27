package com.oneandone.iocunitejb.ejbs;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Timeout;
import jakarta.inject.Inject;

/**
 * @author aschoerk
 */
@Singleton
public class SingletonTimerEJB extends CountingBean {

    @Inject
    StatelessAsynchEJB injectedBean;  // used to check if injection worked

    int i = 0;

    @PostConstruct
    public void postConstruct() {
        setPostConstructCalled();   // used to check if normal lifecyclemethods worked
    }

    @Timeout
    public void callAsynch() {
        if (injectedBean != null) {
            if (isPostConstructCalled()) {
                i++;
                logcall();
                injectedBean.callAsynch(true);
                return;
            } else {
                logger.error("postconstruct did not work for this instance");
            }
        } else {
            logger.error("inject did not work for this instance");
        }
    }
}
