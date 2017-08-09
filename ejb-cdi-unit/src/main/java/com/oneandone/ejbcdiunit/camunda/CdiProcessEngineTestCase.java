/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oneandone.ejbcdiunit.camunda;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.camunda.bpm.engine.AuthorizationService;
import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.DecisionService;
import org.camunda.bpm.engine.ExternalTaskService;
import org.camunda.bpm.engine.FilterService;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.cdi.BusinessProcess;
import org.camunda.bpm.engine.cdi.impl.util.ProgrammaticBeanLookup;
import org.camunda.bpm.engine.impl.jobexecutor.JobExecutor;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Daniel Meyer
 */
@RunWith(JUnit4.class)
public abstract class CdiProcessEngineTestCase {

    protected Logger logger = Logger.getLogger(getClass().getName());
    @Inject
    protected BeanManager beanManager;
    @Inject
    protected ProcessEngine processEngine;
    @Inject
    protected FormService formService;
    @Inject
    protected HistoryService historyService;
    @Inject
    protected IdentityService identityService;
    @Inject
    protected ManagementService managementService;
    @Inject
    protected RepositoryService repositoryService;
    @Inject
    protected RuntimeService runtimeService;
    @Inject
    protected TaskService taskService;
    @Inject
    protected AuthorizationService authorizationService;
    @Inject
    protected FilterService filterService;
    @Inject
    protected ExternalTaskService externalTaskService;
    @Inject
    protected CaseService caseService;
    @Inject
    protected DecisionService decisionService;
    @Inject
    protected BusinessProcess businessProcess;
    @Inject
    CdiUnitContextAssociationManager.ApplicationScopedAssociation applicationScopedAssociation;
    private EjbCamundaUnitRule ejbCamundaUnitRule;

    @Rule
    public EjbCamundaUnitRule getEjbCamundaUnitRule() {
        if (this.ejbCamundaUnitRule != null)
            return ejbCamundaUnitRule;
        else {
            this.ejbCamundaUnitRule = new EjbCamundaUnitRule(this);
            return ejbCamundaUnitRule;
        }
    }

  protected <T> T getBeanInstance(Class<T> clazz) {
    return ProgrammaticBeanLookup.lookup(clazz);
  }

  protected Object getBeanInstance(String name) {
    return ProgrammaticBeanLookup.lookup(name);
  }

  //////////////////////// copied from AbstractActivitiTestcase

  public void waitForJobExecutorToProcessAllJobs(long maxMillisToWait, long intervalMillis) {
        JobExecutor jobExecutor = ejbCamundaUnitRule.getProcessEngineConfiguration().getJobExecutor();
    jobExecutor.start();

    try {
      Timer timer = new Timer();
      InteruptTask task = new InteruptTask(Thread.currentThread());
      timer.schedule(task, maxMillisToWait);
      boolean areJobsAvailable = true;
      try {
        while (areJobsAvailable && !task.isTimeLimitExceeded()) {
          Thread.sleep(intervalMillis);
          areJobsAvailable = areJobsAvailable();
        }
      } catch (InterruptedException e) {
      } finally {
        timer.cancel();
      }
      if (areJobsAvailable) {
        throw new ProcessEngineException("time limit of " + maxMillisToWait + " was exceeded");
      }

    } finally {
      jobExecutor.shutdown();
    }
  }

  public void waitForJobExecutorOnCondition(long maxMillisToWait, long intervalMillis, Callable<Boolean> condition) {
        JobExecutor jobExecutor = ejbCamundaUnitRule.getProcessEngineConfiguration().getJobExecutor();
    jobExecutor.start();

    try {
      Timer timer = new Timer();
      InteruptTask task = new InteruptTask(Thread.currentThread());
      timer.schedule(task, maxMillisToWait);
      boolean conditionIsViolated = true;
      try {
        while (conditionIsViolated) {
          Thread.sleep(intervalMillis);
          conditionIsViolated = !condition.call();
        }
      } catch (InterruptedException e) {
      } catch (Exception e) {
        throw new ProcessEngineException("Exception while waiting on condition: "+e.getMessage(), e);
      } finally {
        timer.cancel();
      }
      if (conditionIsViolated) {
        throw new ProcessEngineException("time limit of " + maxMillisToWait + " was exceeded");
      }

    } finally {
      jobExecutor.shutdown();
    }
  }

  public boolean areJobsAvailable() {
    return !managementService
      .createJobQuery()
      .executable()
      .list()
      .isEmpty();
  }

  private static class InteruptTask extends TimerTask {
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
