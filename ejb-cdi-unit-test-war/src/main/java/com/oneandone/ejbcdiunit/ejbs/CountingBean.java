package com.oneandone.ejbcdiunit.ejbs;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aschoerk
 */
public class CountingBean {


    public static final int INITIALCOUNT = 200;

    private boolean postConstructCalled = false;

    public boolean isPostConstructCalled() {
        return postConstructCalled;
    }

    /**
     * set PostConstructCalled to true.
     */
    public void setPostConstructCalled() {
        postConstructCalled = true;
    }

    /**
     * reset counter of CountingBean
     */
    @PreDestroy
    public void predestroy() {
        counter.set(INITIALCOUNT);
    }

    CountingBean() {
    }

    Logger logger = LoggerFactory.getLogger("CountingBean");

    private static AtomicInteger counter = new AtomicInteger(INITIALCOUNT);

    /**
     * increment counter log it.
     */
    public void logcall() {
        counter.addAndGet(1);
        logger.info("CountingBean called {}", this.getClass().getSimpleName());
    }

    /**
     * get internal counter
     * @return the current value of the counter
     */
    public static int get() {
        return counter.get();
    }
}
