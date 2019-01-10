/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */
package org.camunda.bpm.engine.cdi.cdiunittest.api.annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.camunda.bpm.engine.cdi.cdiunittest.impl.beans.DeclarativeProcessController;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.type.ValueType;
import org.camunda.bpm.engine.variable.value.StringValue;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.jglue.cdiunit.AdditionalPackages;
import org.junit.Test;

import com.oneandone.ejbcdiunit.camunda.CdiProcessEngineTestCase;

/**
 * @author Roman Smirnov
 */
@AdditionalPackages({ DeclarativeProcessController.class })
public class ProcessVariableTypedTest extends CdiProcessEngineTestCase {

    @Inject
    DeclarativeProcessController declarativeProcessController;

    @Test
    @Deployment(resources = "org/camunda/bpm/engine/cdi/cdiunittest/api/annotation/CompleteTaskTest.bpmn20.xml")
    public void testProcessVariableTypeAnnotation() {

        VariableMap variables = Variables.createVariables().putValue("injectedValue", "camunda");
        businessProcess.startProcessByKey("keyOfTheProcess", variables);

        TypedValue value = declarativeProcessController.getInjectedValue();
        assertNotNull(value);
        assertTrue(value instanceof StringValue);
        assertEquals(ValueType.STRING, value.getType());
        assertEquals("camunda", value.getValue());
    }

}
