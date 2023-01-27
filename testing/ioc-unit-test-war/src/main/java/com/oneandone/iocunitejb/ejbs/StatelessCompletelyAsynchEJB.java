package com.oneandone.iocunitejb.ejbs;

import java.util.concurrent.Future;

import jakarta.ejb.AsyncResult;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;

/**
 * @author aschoerk
 */
@Stateless
@Asynchronous
public class StatelessCompletelyAsynchEJB extends CountingBean {

    public Future<Boolean> callAsynch(boolean result) {
        logcall();
        return new AsyncResult<>(result);
    }

}
