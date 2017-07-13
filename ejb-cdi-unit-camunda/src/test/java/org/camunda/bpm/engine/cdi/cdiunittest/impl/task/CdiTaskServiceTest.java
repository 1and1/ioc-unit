package org.camunda.bpm.engine.cdi.cdiunittest.impl.task;

import org.camunda.bpm.engine.cdi.cdiunittest.CdiProcessEngineTestCase;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Test;


public class CdiTaskServiceTest extends CdiProcessEngineTestCase {
  
  @Test
  public void testClaimTask() {
    Task newTask = taskService.newTask();
    taskService.saveTask(newTask);
    taskService.claim(newTask.getId(), "kermit");
    taskService.deleteTask(newTask.getId(),true);
  }

}
