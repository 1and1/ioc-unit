package org.camunda.bpm.engine.cdi.cdiunittest.impl.util;

import java.util.TimerTask;

import javax.inject.Inject;

import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.cdi.BusinessProcess;
import org.camunda.bpm.engine.impl.jobexecutor.JobExecutor;
import org.junit.Rule;

import com.oneandone.ejbcdiunit.AsynchronousManager;
import com.oneandone.ejbcdiunit.camunda.EjbCamundaUnitRule;

/**
 * @author aschoerk
 */
public class BaseTest {
    @Inject
    protected RuntimeService runtimeService;

    @Inject
    protected ManagementService managementService;

    @Inject
    protected AsynchronousManager asynchronousManager;

    @Inject
    protected BusinessProcess businessProcess;

    @Inject
    protected TaskService taskService;

    private EjbCamundaUnitRule ejbCamundaUnitRule;

    @Rule
    public EjbCamundaUnitRule getEjbCamundaUnitRule() {
        ejbCamundaUnitRule = EjbCamundaUnitRule.createRuleWithAsynchronousManager(this, null);
        return ejbCamundaUnitRule;
    }


    public boolean areJobsAvailable() {
        return !managementService
                .createJobQuery()
                .executable()
                .list()
                .isEmpty();
    }

    public void waitForJobExecutorToProcessAllJobs() {
        JobExecutor jobExecutor = ejbCamundaUnitRule.getProcessEngineConfiguration().getJobExecutor();
        jobExecutor.start();
        asynchronousManager.untilNothingLeft();
    }

    protected static class InteruptTask extends TimerTask {
        protected boolean timeLimitExceeded = false;
        protected Thread thread;

        public InteruptTask(Thread thread) {
            this.thread = thread;
        }

        public boolean isTimeLimitExceeded() {
            return timeLimitExceeded;
        }

        @Override
        public void run() {
            timeLimitExceeded = true;
            thread.interrupt();
        }
    }

}
