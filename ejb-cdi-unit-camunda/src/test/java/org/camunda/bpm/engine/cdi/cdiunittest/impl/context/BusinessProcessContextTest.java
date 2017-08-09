/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */
package org.camunda.bpm.engine.cdi.cdiunittest.impl.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.camunda.bpm.engine.cdi.cdiunittest.impl.beans.CreditCard;
import org.camunda.bpm.engine.cdi.cdiunittest.impl.beans.ProcessScopedMessageBean;
import org.camunda.bpm.engine.cdi.cdiunittest.impl.util.BaseTest;
import org.camunda.bpm.engine.cdi.impl.util.ProgrammaticBeanLookup;
import org.camunda.bpm.engine.test.Deployment;
import org.jglue.cdiunit.AdditionalPackages;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Daniel Meyer
 */
@AdditionalPackages({ CreditCard.class })
public class BusinessProcessContextTest extends BaseTest {


    @Inject
    ProcessScopedMessageBean processScopedMessageBean;
    @Inject
    CreditCard creditCard;

    @Test
    @Deployment
    public void testResolution() throws Exception {


        businessProcess.startProcessByKey("testResolution").getId();

        assertNotNull(creditCard);
    }

    @Test
    // no @Deployment for this test
    public void testResolutionBeforeProcessStart() throws Exception {
        // assert that @businessProcessScoped beans can be resolved in the absence of an underlying process instance:
        assertNotNull(creditCard);
    }

    @Test
    @Deployment
    public void testConversationalBeanStoreFlush() throws Exception {

        businessProcess.setVariable("testVariable", "testValue");
        String pid = businessProcess.startProcessByKey("testConversationalBeanStoreFlush").getId();

        businessProcess.associateExecutionById(pid);

        // assert that the variable assigned on the businessProcess bean is flushed
        assertEquals("testValue", runtimeService.getVariable(pid, "testVariable"));

        // assert that the value set to the message bean in the first service task is flushed
        Assert.assertEquals("Hello from Activiti", processScopedMessageBean.getMessage());

        // complete the task to allow the process instance to terminate
        taskService.complete(taskService.createTaskQuery().singleResult().getId());
    }

    @Test
    @Deployment
    public void testChangeProcessScopedBeanProperty() throws Exception {

        // resolve the creditcard bean (@businessProcessScoped) and set a value:
        creditCard.setCreditcardNumber("123");
        String pid = businessProcess.startProcessByKey("testConversationalBeanStoreFlush").getId();

        businessProcess.startTask(taskService.createTaskQuery().singleResult().getId());

        // assert that the value of creditCardNumber is '123'
        assertEquals("123", ProgrammaticBeanLookup.lookup(CreditCard.class).getCreditcardNumber());
        // set a different value:
        creditCard.setCreditcardNumber("321");
        // complete the task
        businessProcess.completeTask();

        businessProcess.associateExecutionById(pid);

        // now assert that the value of creditcard is "321":
        assertEquals("321", ProgrammaticBeanLookup.lookup(CreditCard.class).getCreditcardNumber());

        // complete the task to allow the process instance to terminate
        taskService.complete(taskService.createTaskQuery().singleResult().getId());

    }

}
