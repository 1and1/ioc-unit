/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */
package org.camunda.bpm.engine.cdi.cdiunittest.api.annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.camunda.bpm.engine.cdi.cdiunittest.impl.beans.DeclarativeProcessController;
import org.camunda.bpm.engine.cdi.impl.annotation.StartProcessInterceptor;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.variable.type.ValueType;
import org.camunda.bpm.engine.variable.value.StringValue;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.jglue.cdiunit.AdditionalPackages;
import org.junit.Test;

import com.oneandone.ejbcdiunit.camunda.CdiProcessEngineTestCase;

/**
 * Testcase for assuring that the {@link StartProcessInterceptor} behaves as expected.
 *
 * @author Daniel Meyer
 */
@AdditionalPackages({ DeclarativeProcessController.class })
public class StartProcessTest extends CdiProcessEngineTestCase {

    @Inject
    DeclarativeProcessController declarativeProcessController;

    @Test
    @Deployment(resources = "org/camunda/bpm/engine/cdi/cdiunittest/api/annotation/StartProcessTest.bpmn20.xml")
    public void testStartProcessByKey() {

        assertNull(runtimeService.createProcessInstanceQuery().singleResult());

        declarativeProcessController.startProcessByKey();


        assertNotNull(runtimeService.createProcessInstanceQuery().singleResult());

        assertEquals("camunda", businessProcess.getVariable("name"));

        TypedValue nameTypedValue = businessProcess.getVariableTyped("name");
        assertNotNull(nameTypedValue);
        assertTrue(nameTypedValue instanceof StringValue);
        assertEquals(ValueType.STRING, nameTypedValue.getType());
        assertEquals("camunda", nameTypedValue.getValue());

        assertEquals("untypedName", businessProcess.getVariable("untypedName"));

        TypedValue untypedNameTypedValue = businessProcess.getVariableTyped("untypedName");
        assertNotNull(untypedNameTypedValue);
        assertTrue(untypedNameTypedValue instanceof StringValue);
        assertEquals(ValueType.STRING, untypedNameTypedValue.getType());
        assertEquals("untypedName", untypedNameTypedValue.getValue());


        assertEquals("typedName", businessProcess.getVariable("typedName"));

        TypedValue typedNameTypedValue = businessProcess.getVariableTyped("typedName");
        assertNotNull(typedNameTypedValue);
        assertTrue(typedNameTypedValue instanceof StringValue);
        assertEquals(ValueType.STRING, typedNameTypedValue.getType());
        assertEquals("typedName", typedNameTypedValue.getValue());

        businessProcess.startTask(taskService.createTaskQuery().singleResult().getId());
        businessProcess.completeTask();
    }


}
