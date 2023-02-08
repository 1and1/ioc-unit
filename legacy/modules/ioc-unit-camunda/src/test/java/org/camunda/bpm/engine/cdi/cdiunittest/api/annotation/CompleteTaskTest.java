/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */
package org.camunda.bpm.engine.cdi.cdiunittest.api.annotation;

import static org.junit.Assert.assertNull;

import jakarta.inject.Inject;

import org.camunda.bpm.engine.cdi.cdiunittest.impl.beans.DeclarativeProcessController;
import org.camunda.bpm.engine.cdi.impl.annotation.CompleteTaskInterceptor;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Test;

import com.oneandone.iocunitejb.camunda.CdiProcessEngineTestCase;

/**
 * Testcase for assuring that the {@link CompleteTaskInterceptor} works as expected
 * 
 * @author Daniel Meyer
 */
public class CompleteTaskTest extends CdiProcessEngineTestCase {

    @Inject
    DeclarativeProcessController declarativeProcessController;

    @Test
    @Deployment(resources = "org/camunda/bpm/engine/cdi/cdiunittest/api/annotation/CompleteTaskTest.bpmn20.xml")
    public void testCompleteTask() {


        businessProcess.startProcessByKey("keyOfTheProcess");

        Task task = taskService.createTaskQuery().singleResult();

        // associate current unit of work with the task:
        businessProcess.startTask(task.getId());

        declarativeProcessController.completeTask();

        // assert that now the task is completed
        assertNull(taskService.createTaskQuery().singleResult());
    }


}
