package com.oneandone.ejbcdiunit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.ejb.Schedule;
import javax.ejb.Schedules;
import javax.ejb.Timeout;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.ejbcdiunit.internal.EjbExtensionExtended;
import com.oneandone.ejbcdiunit.persistence.SimulatedTransactionManager;

/**
 * Singleton used to store asynchronous calls which can be done later at specific times as fitting to the test.
 *
 * @author aschoerk
 */
@ApplicationScoped
public class AsynchronousManager {

    private static final int TIME_TO_WAIT_AFTER_HANDLING = 200;
    private static final int TIME_TO_WAIT_FOR_INTERRUPT = 200;
    private static final int TIME_TO_STOP_LOOPING_ONCE = 200;
    @Inject
    SimulatedTransactionManager transactionManager;
    @Inject
    EjbExtensionExtended ejbExtensionExtended;
    @Inject
    BeanManager bm;
    private Logger logger = LoggerFactory.getLogger("AsynchronousManager");
    private CreationalContexts creationalContexts = null;

    private ArrayList<AsynchronousRunnable> runnables = new ArrayList<>();

    private boolean enqueAsynchronousCalls = false;
    private Thread asyncHandler = null;

    /**
     * check if Asynchronous Methods may be dispatched
     *
     * @return false, if Asynchronous Methods should get executed immediately, without using the AsynchronousManager.
     */
    public boolean doesEnqueAsynchronousCalls() {
        return enqueAsynchronousCalls;
    }

    /**
     * set if Asynchronous Methods should be dispatched
     *
     * @param enqueAsynchronousCalls1 true, if they should be dispatched, false if they are to be executed immediately,
     *                               without AsynchronousManager
     */
    public void setEnqueAsynchronousCalls(boolean enqueAsynchronousCalls1) {
        this.enqueAsynchronousCalls = enqueAsynchronousCalls1;
    }

    /**
     * find timer-called methods in classes prepared by EjbExtension and add these to this Asynchronous Manager.
     *
     * @return a String containing comma-separated all registered Methods in the form "classname"#"methodname"
     */
    String addTimerMethods() {
        if (creationalContexts != null) {
            throw new RuntimeException("Second call of addTimerMethods on AsynchronousManager");
        }
        StringBuilder sb = new StringBuilder();
        List<Class<?>> timerClasses = ejbExtensionExtended.getTimerClasses();
        creationalContexts = new CreationalContexts(bm);

        for (Class timerClass: timerClasses) {
            Set<Bean<?>> beans = bm.getBeans(timerClass);
            for (Bean<?> b: beans) {
                Class<?> c = b.getBeanClass();
                Method[] methods = c.getMethods();
                for (final Method m : methods) {
                    if (m.getAnnotation(Schedules.class) != null
                            || m.getAnnotation(Schedule.class) != null
                            || m.getAnnotation(Timeout.class) != null
                            ) {
                        if (m.getParameterTypes().length > 0) {
                            logger.error("Can not handle automatically Bean with class {} and TimeoutMethod {}", c.getCanonicalName(), m.getName());
                        } else {
                            final Object o = creationalContexts.create(b, Dependent.class);
                            addMultipleHandler(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        m.invoke(o);
                                    } catch (IllegalAccessException | InvocationTargetException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                            if (sb.length() > 0) {
                                sb.append(",");
                            }
                            sb.append(c.getCanonicalName());
                            sb.append("#");
                            sb.append(m.getName());
                            logger.info("Installed Timer for Class: {}, Method: {} ", c.getSimpleName(), m.getName());
                        }
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * start Thread periodically calling asynchronous manager by itself, if not yet initialized,
     */
    public void startThread() {
        if (asyncHandler == null) {
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
                        if (done > 0) {
                            logger.info("AsynchronousManager handled {} runners", done);
                        }
                        while (thereAreOnces() && System.currentTimeMillis() - time < TIME_TO_STOP_LOOPING_ONCE) {
                            done = once();
                            if (done > 0) {
                                logger.info("AsynchronousManager handled {} runners", done);
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
                logger.info("AsynchronousManager Thread after loop.");
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
        if (asyncHandler != null) {
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
        if (creationalContexts != null) {
            creationalContexts.closeIt();
        }
        if (!runnables.isEmpty()) {
            logger.error("There are {} runnables for left for this Test. Possibly no Consumer created or AsynchronousManager not polled.", runnables.size());
            throw new RuntimeException("There are runnables for left for this Test. Possibly no Consumer created or AsynchronousManager not polled");
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
                try {
                    transactionManager.push(TransactionAttributeType.NOT_SUPPORTED);
                    runnable.run();
                } catch (InterruptThreadException e) {
                    logger.info("Asynchronous Manager Thread received end signal");
                    throw e;
                } catch (Throwable thw) {
                    logger.error("Error during OneTimeHandler", thw);
                } finally {
                    try {
                        transactionManager.pop();
                    } catch (Exception e) {
                        logger.error("AsynchronousManager catched: {} during TransactionManager#pop.", e.getMessage(), " no further handling");
                    }
                }
            }
        });
    }

    /**
     * Register a Handler not called only once, so it should not get removed during call-loop.
     *
     * @param runnable
     *            the code to be called.
     */
    public synchronized void addMultipleHandler(final Runnable runnable) {
        runnables.add(new AsynchronousMultipleRunnable() {
            @Override
            public void run() {
                try {
                    transactionManager.push(TransactionAttributeType.NOT_SUPPORTED);
                    runnable.run();
                } catch (InterruptThreadException e) {
                    logger.info("Asynchronous Manager Thread received end signal");
                    throw e;
                } catch (Throwable thw) {
                    logger.error("Error during MultipleHandler", thw);
                } finally {
                    try {
                        transactionManager.pop();
                    } catch (Exception e) {
                        logger.error("AsynchronousManager catched: {} during TransactionManager#pop.", e.getMessage(), " no further handling");
                    }
                }
            }
        });
    }

    private synchronized List<AsynchronousRunnable> cloneRunnables() {
        ArrayList<AsynchronousRunnable> result = new ArrayList<>(runnables.size());
        result.addAll(runnables);
        if (result.size() > 1) {
            logger.info("Encountered more than one possibility to run asynchronous, different sequences might produce different results.");
        }
        return result;
    }

    private synchronized void remove(AsynchronousRunnable runner) {
        runnables.remove(runner);
    }

    /**
     * call all registered Runables just once.
     * @return number of runnables actually called
     */
    public int once() {
        int runnersCount = 0;
        List<AsynchronousRunnable> runners = cloneRunnables();
        for (AsynchronousRunnable runner: runners) {
            runner.run();
            runnersCount++;
            if (runner.oneShotOnly()) {
                remove(runner);
            }
        }
        return runnersCount;
    }

    /**
     * call all registered Runables just once.
     * @return number of runnables actually called
     */
    public int oneShotOnly() {
        int runnersCount = 0;
        List<AsynchronousRunnable> runners = cloneRunnables();
        for (AsynchronousRunnable runner: runners) {
            if (runner.oneShotOnly()) {
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
            if (runners.isEmpty()) {
                return;
            }
            for (AsynchronousRunnable runner : runners) {
                runner.run();
                if (runner.oneShotOnly()) {
                    remove(runner);
                }
            }
        } while (!predicate.stopCalling());
    }

    /**
     * call the runnables until there are no more left this does not work when timer methods are registered.
     *
     */
    public void untilNothingLeft() {
        until(new AsynchronousCallEndCondition() {
            @Override
            public boolean stopCalling() {
                for (AsynchronousRunnable ar : runnables) {
                    if (ar.oneShotOnly())
                        return false;
                }
                return true;
            }
        });
    }

    public boolean thereAreOnces() {
        for (AsynchronousRunnable r: runnables) {
            if (r instanceof AsynchronousOnetimeRunnable) {
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
    private static class InterruptThreadException extends RuntimeException {

        private static final long serialVersionUID = -1786916994010029037L;
    }

    /**
     * defines an asynchronous call done only once
     */
    private abstract static class AsynchronousOnetimeRunnable implements AsynchronousRunnable {
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
