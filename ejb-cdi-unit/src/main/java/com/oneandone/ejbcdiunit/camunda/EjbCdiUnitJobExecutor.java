package com.oneandone.ejbcdiunit.camunda;

import java.util.Iterator;
import java.util.List;

import org.camunda.bpm.engine.cdi.impl.util.ProgrammaticBeanLookup;
import org.camunda.bpm.engine.impl.ProcessEngineImpl;
import org.camunda.bpm.engine.impl.jobexecutor.AcquiredJobs;
import org.camunda.bpm.engine.impl.jobexecutor.JobAcquisitionContext;
import org.camunda.bpm.engine.impl.jobexecutor.JobAcquisitionStrategy;
import org.camunda.bpm.engine.impl.jobexecutor.JobExecutor;
import org.camunda.bpm.engine.impl.jobexecutor.SequentialJobAcquisitionRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.ejbcdiunit.AsynchronousManager;

/**
 * Camunda JobExecutor using AsynchronousManager.
 *
 * @author aschoerk
 */
public class EjbCdiUnitJobExecutor extends JobExecutor {

    static ThreadLocal<EjbCdiUnitJobExecutor> executorInThread = new ThreadLocal<>();
    Logger logger = LoggerFactory.getLogger("EjbCdiUnitJobExecutor");
    AsynchronousManager asynchronousManager;

    public EjbCdiUnitJobExecutor() {

    }

    static public void initThreadLocal() {
        EjbCdiUnitJobExecutor executor = executorInThread.get();
        if (executor != null) {
            executorInThread.set(null);
            executor.startExecutingJobs();
        }
    }

    public AsynchronousManager getAsynchronousManager() {
        return asynchronousManager;
    }

    @Override
    protected void startExecutingJobs() {
        if (executorInThread.get() != null) {
            asynchronousManager = executorInThread.get().getAsynchronousManager();
        } else {
            asynchronousManager = ProgrammaticBeanLookup.lookup(AsynchronousManager.class);
            executorInThread.set(this);
        }
        asynchronousManager.addMultipleHandler(new SequentialJobAcquisitionRunnable(this) {
            @Override
            public synchronized void run() {

                acquisitionContext.reset();
                acquisitionContext.setAcquisitionTime(System.currentTimeMillis());
                // we are in a new acquisition cycle; discard any previous notification
                clearJobAddedNotification();

                Iterator<ProcessEngineImpl> engineIterator = jobExecutor.engineIterator();

                JobAcquisitionStrategy acquisitionStrategy = new JobAcquisitionStrategy() {
                    @Override
                    public void reconfigure(JobAcquisitionContext context) {

                    }

                    @Override
                    public long getWaitTime() {
                        return 0;
                    }

                    @Override
                    public int getNumJobsToAcquire(String processEngine) {
                        return 100;
                    }
                };
                try {
                    while (engineIterator.hasNext()) {
                        ProcessEngineImpl currentProcessEngine = engineIterator.next();
                        if (!jobExecutor.hasRegisteredEngine(currentProcessEngine)) {
                            // if engine has been unregistered meanwhile
                            continue;
                        }

                        AcquiredJobs acquiredJobs = acquireJobs(acquisitionContext, acquisitionStrategy, currentProcessEngine);
                        executeJobs(acquisitionContext, currentProcessEngine, acquiredJobs);
                    }
                } catch (Exception e) {
                    logger.error("Exception during Aquisition", e);

                    acquisitionContext.setAcquisitionException(e);
                }

                acquisitionContext.setJobAdded(isJobAdded);
                configureNextAcquisitionCycle(acquisitionContext, acquisitionStrategy);

                long waitTime = acquisitionStrategy.getWaitTime();
                // wait the requested wait time minus the time that acquisition itself took
                // this makes the intervals of job acquisition more constant and therefore predictable
                waitTime = Math.max(0, (acquisitionContext.getAcquisitionTime() + waitTime) - System.currentTimeMillis());

            }
        });
        // super.startJobAcquisitionThread();
        System.out.println("output");
    }

    @Override
    protected void stopExecutingJobs() {
        try {
            if (asynchronousManager.thereAreOnces())
                logger.error("There are jobs left to be executed");
        } catch (Throwable thw) {
            logger.error("exception in stopping executor", thw);
        }
        executorInThread.set(null);
    }

    @Override
    public void executeJobs(List<String> jobIds, ProcessEngineImpl processEngine) {
        asynchronousManager.addOneTimeHandler(getExecuteJobsRunnable(jobIds, processEngine));
    }
}
