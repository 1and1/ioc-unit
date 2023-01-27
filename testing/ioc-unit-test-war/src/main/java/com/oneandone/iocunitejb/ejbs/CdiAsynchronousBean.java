package com.oneandone.iocunitejb.ejbs;

import java.util.concurrent.Future;

import jakarta.ejb.AsyncResult;
import jakarta.ejb.Asynchronous;
import jakarta.enterprise.context.ApplicationScoped;

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
