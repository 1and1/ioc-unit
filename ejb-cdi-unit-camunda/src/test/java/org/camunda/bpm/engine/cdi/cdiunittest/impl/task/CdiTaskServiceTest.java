package org.camunda.bpm.engine.cdi.cdiunittest.impl.task;

import org.camunda.bpm.engine.task.Task;
import org.junit.Test;

import com.oneandone.ejbcdiunit.camunda.CdiProcessEngineTestCase;


public class CdiTaskServiceTest extends CdiProcessEngineTestCase {

    @Test
    public void testClaimTask() {
        Task newTask = taskService.newTask();
        taskService.saveTask(newTask);
        taskService.claim(newTask.getId(), "kermit");
        taskService.deleteTask(newTask.getId(), true);
    }

}
