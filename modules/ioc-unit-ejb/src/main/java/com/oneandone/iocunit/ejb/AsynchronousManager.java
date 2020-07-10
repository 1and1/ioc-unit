package com.oneandone.iocunit.ejb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Schedules;
import javax.ejb.Timeout;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.inject.Inject;
import javax.transaction.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.cdi.weldstarter.CreationalContexts;
import com.oneandone.iocunit.AsynchronousSimulator;
import com.oneandone.iocunit.ejb.jms.EjbJmsInitializer;
import com.oneandone.iocunit.ejb.persistence.SimulatedTransactionManager;

/**
 * Singleton used to store asynchronous calls which can be done later at specific times as fitting to the test.
 *
 * @author aschoerk
 */
@ApplicationScoped
public class AsynchronousManager extends AsynchronousSimulator {

    @Inject
    private SimulatedTransactionManager transactionManager;
    @Inject
    private EjbExtensionExtended ejbExtensionExtended;

    private Logger logger = LoggerFactory.getLogger("AsynchronousManager");

    @Inject
    Instance<EjbJmsInitializer> jmsInitializer;

    @PostConstruct
    public void postConstructJmsAsynchManager() {
        try {
            if(!jmsInitializer.isUnsatisfied()) {
                jmsInitializer.get().dummyCall();
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }

    /**
     * find timer-called methods in classes prepared by EjbExtension and add these to this Asynchronous Manager.
     *
     * @return a String containing comma-separated all registered Methods in the form "classname"#"methodname"
     */
    String addTimerMethods() {
        if(creationalContexts != null) {
            throw new RuntimeException("Second call of addTimerMethods on AsynchronousSimulator");
        }
        StringBuilder sb = new StringBuilder();
        Set<Class<?>> timerClasses = ejbExtensionExtended.getTimerClasses();
        creationalContexts = new CreationalContexts(bm);

        logger.debug("Start adding Timermethods to Asynchronous Manager");
        for (Class timerClass : timerClasses) {
            Set<Bean<?>> beans = bm.getBeans(timerClass);
            for (Bean<?> b : beans) {
                Class<?> c = b.getBeanClass();
                Method[] methods = c.getMethods();
                for (final Method m : methods) {
                    if(m.getAnnotation(Schedules.class) != null
                       || m.getAnnotation(Schedule.class) != null
                       || m.getAnnotation(Timeout.class) != null
                    ) {
                        if(m.getParameterTypes().length > 0) {
                            logger.error("Can not handle automatically Bean with class {} and TimeoutMethod {}", c.getCanonicalName(), m.getName());
                        }
                        else {
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
                            if(sb.length() > 0) {
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
        logger.debug("Ready adding Timermethods to Asynchronous Manager");
        return sb.toString();
    }

    @Override
    protected void callRunnable(final Runnable runnable, final String s) {
        try {
            try {
                if(userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION) {
                    userTransaction.commit();
                    userTransaction.begin();
                }
            } catch (Exception e) {
                logger.trace("Ignored {} in AsynchronousSimulator", e.getMessage());
            }
            transactionManager.push(TransactionAttributeType.NOT_SUPPORTED);
            runnable.run();
        } catch (InterruptThreadException e) {
            logger.info("Asynchronous Manager Thread received end signal");
            throw e;
        } catch (Throwable thw) {
            logger.error(s, thw);
        } finally {
            try {
                transactionManager.pop();
            } catch (Exception e) {
                logger.error("AsynchronousSimulator catched: {} during TransactionManager#pop.", e.getMessage(), " no further handling");
            }
        }
    }

}
