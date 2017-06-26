package com.oneandone.ejbcdiunit.ejbs;

import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.enterprise.context.ApplicationScoped;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class CdiAsynchronousBean extends CountingBean {

    @Asynchronous
    public Future<Boolean> callAsynchSleep1000(boolean result) throws InterruptedException {
        Thread.sleep(1000);
        logcall();
        return new AsyncResult<>(result);
    }
}
