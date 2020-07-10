package com.oneandone.iocunit;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.cdi.weldstarter.CreationalContexts;

/**
 * Singleton used to store asynchronous calls which can be done later at specific times as fitting to the test.
 *
 * @author aschoerk
 */
@ApplicationScoped
public class AsynchronousSimulator {

    private static final int TIME_TO_WAIT_AFTER_HANDLING = 200;
    private static final int TIME_TO_WAIT_FOR_INTERRUPT = 200;
    private static final int TIME_TO_STOP_LOOPING_ONCE = 200;

    @Inject
    protected BeanManager bm;

    @Inject
    protected UserTransaction userTransaction;
    protected CreationalContexts creationalContexts = null;
    private Logger logger = LoggerFactory.getLogger("AsynchronousSimulator");
    private ArrayList<AsynchronousRunnable> runnables = new ArrayList<>();

    private boolean enqueAsynchronousCalls = false;
    private Thread asyncHandler = null;


    /**
     * check if Asynchronous Methods may be dispatched
     *
     * @return false, if Asynchronous Methods should get executed immediately, without using the AsynchronousSimulator.
     */
    public boolean doesEnqueAsynchronousCalls() {
        return enqueAsynchronousCalls;
    }

    /**
     * set if Asynchronous Methods should be dispatched
     *
     * @param enqueAsynchronousCalls1 true, if they should be dispatched, false if they are to be executed immediately,
     *                                without AsynchronousSimulator
     */
    public void setEnqueAsynchronousCalls(boolean enqueAsynchronousCalls1) {
        this.enqueAsynchronousCalls = enqueAsynchronousCalls1;
    }


    /**
     * start Thread periodically calling asynchronous manager by itself, if not yet initialized,
     */
    public void startThread() {
        if(asyncHandler == null) {
            initThread();
        }
    }

    private void initThread() {
        asyncHandler = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    long time = System.currentTimeMillis();
                    try {
                        int done = once();
                        if(done > 0) {
                            logger.info("AsynchronousSimulator handled {} runners", done);
                        }
                        while (thereAreOnces() && System.currentTimeMillis() - time < TIME_TO_STOP_LOOPING_ONCE) {
                            done = once();
                            if(done > 0) {
                                logger.info("AsynchronousSimulator handled {} runners", done);
                            }
                        }
                    } catch (InterruptThreadException e) {
                        logger.info("Asynchronous Manager Thread received end signal");
                        break;
                    } catch (Throwable thw) {
                        logger.error("Asynchronous Manager intercepted: ", thw);
                    }
                    try {
                        Thread.sleep(TIME_TO_WAIT_AFTER_HANDLING);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                logger.info("AsynchronousSimulator Thread after loop.");
            }
        });
        asyncHandler.setDaemon(true);
        asyncHandler.start();
        logger.info("Thread started");

    }

    /**
     * stop asynchronous handler thread.
     */
    public void stopThread() {
        if(asyncHandler != null) {
            logger.info("start stopping thread");
            addOneTimeHandler(new Runnable() {
                @Override
                public void run() {
                    throw new InterruptThreadException();
                }
            });
            while (asyncHandler.isAlive()) {
                try {
                    Thread.sleep(TIME_TO_WAIT_FOR_INTERRUPT);
                    logger.info("waiting for thread to stop");
                } catch (InterruptedException e) {
                    logger.error("Thread interrupted");
                }
            }
            logger.info("ready stopping thread");
            asyncHandler = null;
        }
    }

    @PreDestroy
    private void preDestroy() {
        stopThread();
        if(creationalContexts != null) {
            creationalContexts.closeIt();
        }
        if(!runnables.isEmpty()) {
            logger.error("There are {} runnables for left for this Test. Possibly no Consumer created or AsynchronousSimulator not polled.", runnables.size());
            throw new RuntimeException("There are runnables for left for this Test. Possibly no Consumer created or AsynchronousSimulator not polled");
        }
    }

    /**
     * Register a Handler only called once.
     *
     * @param runnable the code to be called. After the call, The Asynchronous Manager removes all references to this.
     */
    public synchronized void addOneTimeHandler(final Runnable runnable) {
        runnables.add(new AsynchronousOnetimeRunnable() {
            @Override
            public void run() {
                callRunnable(runnable, "Error during OneTimeHandler");
            }
        });
    }

    protected void callRunnable(final Runnable runnable, final String s) {
        try {
            runnable.run();
        } catch (InterruptThreadException e) {
            logger.info("Asynchronous Manager Thread received end signal");
            throw e;
        } catch (Throwable thw) {
            logger.error(s, thw);
        } finally {
        }
    }

    /**
     * Register a Handler not called only once, so it should not get removed during call-loop.
     *
     * @param runnable the code to be called.
     */
    public synchronized void addMultipleHandler(final Runnable runnable) {
        runnables.add(new AsynchronousMultipleRunnable() {
            @Override
            public void run() {
                callRunnable(runnable, "Error during MultipleHandler");
            }
        });
    }

    private synchronized List<AsynchronousRunnable> cloneRunnables() {
        ArrayList<AsynchronousRunnable> result = new ArrayList<>(runnables.size());
        result.addAll(runnables);
        if(result.size() > 1) {
            logger.info("Encountered more than one possibility to run asynchronous, different sequences might produce different results.");
        }
        return result;
    }

    private synchronized void remove(AsynchronousRunnable runner) {
        runnables.remove(runner);
    }

    /**
     * call all registered Runables just once.
     *
     * @return number of runnables actually called
     */
    public int once() {
        int runnersCount = 0;
        List<AsynchronousRunnable> runners = cloneRunnables();
        for (AsynchronousRunnable runner : runners) {
            runner.run();
            runnersCount++;
            if(runner.oneShotOnly()) {
                remove(runner);
            }
        }
        return runnersCount;
    }

    /**
     * call all registered Runables just once.
     *
     * @return number of runnables actually called
     */
    public int oneShotOnly() {
        int runnersCount = 0;
        List<AsynchronousRunnable> runners = cloneRunnables();
        for (AsynchronousRunnable runner : runners) {
            if(runner.oneShotOnly()) {
                runner.run();
                runnersCount++;
                remove(runner);
            }
        }
        return runnersCount;
    }

    /**
     * call the runnables until there are no more left or the predicate returns true.
     *
     * @param predicate the condition, if true then end calling.
     */
    public void until(AsynchronousCallEndCondition predicate) {
        do {
            List<AsynchronousRunnable> runners = cloneRunnables();
            if(runners.isEmpty()) {
                return;
            }
            for (AsynchronousRunnable runner : runners) {
                runner.run();
                if(runner.oneShotOnly()) {
                    remove(runner);
                }
            }
        } while (!predicate.stopCalling());
    }

    /**
     * call the runnables until there are no more left this does not work when timer methods are registered.
     */
    public void untilNothingLeft() {
        until(new AsynchronousCallEndCondition() {
            @Override
            public boolean stopCalling() {
                for (AsynchronousRunnable ar : runnables) {
                    if(ar.oneShotOnly()) {
                        return false;
                    }
                }
                return true;
            }
        });
    }

    public boolean thereAreOnces() {
        for (AsynchronousRunnable r : runnables) {
            if(r instanceof AsynchronousOnetimeRunnable) {
                return true;
            }
        }
        return false;
    }

    /**
     * Interface for Lambda used to define endcondition of call-loop
     */
    public interface AsynchronousCallEndCondition {
        /**
         * this is the lambda function.
         *
         * @return true if the end of running condition is reached
         */
        boolean stopCalling();
    }

    /**
     *
     */
    private interface AsynchronousRunnable {
        /**
         * start the logic of this asynchronous call.
         */
        void run();

        /**
         * used to decide, whether this should get removed from original array after execution.
         *
         * @return true it this should not be called more than once
         */
        boolean oneShotOnly();
    }

    /**
     * Used to communicate end of Thread to the while(true)
     */
    protected static class InterruptThreadException extends RuntimeException {

        private static final long serialVersionUID = -1786916994010029037L;
    }

    /**
     * defines an asynchronous call done only once
     */
    protected abstract static class AsynchronousOnetimeRunnable implements AsynchronousRunnable {
        @Override
        public boolean oneShotOnly() {
            return true;
        }
    }

    /**
     * defines an asynchronous call done not only once, i.e. timer calls.
     */
    private abstract static class AsynchronousMultipleRunnable implements AsynchronousRunnable {
        @Override
        public boolean oneShotOnly() {
            return false;
        }
    }


}
