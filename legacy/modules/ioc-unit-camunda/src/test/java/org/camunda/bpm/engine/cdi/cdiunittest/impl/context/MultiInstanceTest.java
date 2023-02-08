/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */
package org.camunda.bpm.engine.cdi.cdiunittest.impl.context;

import java.util.Arrays;

import org.camunda.bpm.engine.cdi.cdiunittest.impl.context.beans.LocalVariableBean;
import org.camunda.bpm.engine.cdi.cdiunittest.impl.util.BaseTest;
import org.camunda.bpm.engine.test.Deployment;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.Test;

/**
 * @author Daniel Meyer
 */
@AdditionalClasses({ LocalVariableBean.class })
public class MultiInstanceTest extends BaseTest {


    @Test
    @Deployment
    public void testParallelMultiInstanceServiceTasks() {
        businessProcess.setVariable("list", Arrays.asList(new String[] { "1", "2" }));
        businessProcess.startProcessByKey("miParallelScriptTask");
    }

    @Test
    @Deployment
    public void testParallelMultiInstanceServiceTasks2() {
        businessProcess.setVariable("list", Arrays.asList(new String[] { "1", "2" }));
        businessProcess.startProcessByKey("miParallelScriptTask");
    }
}
