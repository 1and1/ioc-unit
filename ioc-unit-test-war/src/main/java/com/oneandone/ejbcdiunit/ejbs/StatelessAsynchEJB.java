package com.oneandone.ejbcdiunit.ejbs;

import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;

/**
 * @author aschoerk
 */
@Stateless
public class StatelessAsynchEJB extends CountingBean  {

    @Asynchronous
    public Future<Boolean> callAsynch(boolean result) {
        logcall();
        return new AsyncResult<>(result);
    }

    @Asynchronous
    public Future<Boolean> callAsynchSleep1000(boolean result) throws InterruptedException {
        Thread.sleep(1000);
        logcall();
        return new AsyncResult<>(result);
    }

    public int notAsynchronousMethodReturnsOneImmediately() {
        return 1;
    }
}
