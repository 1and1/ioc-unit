package com.oneandone.ejbcdiunit.ejbs;

import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;

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
