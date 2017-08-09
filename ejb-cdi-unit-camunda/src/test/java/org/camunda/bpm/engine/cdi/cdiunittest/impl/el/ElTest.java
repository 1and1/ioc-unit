/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */
package org.camunda.bpm.engine.cdi.cdiunittest.impl.el;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import javax.inject.Inject;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.cdi.cdiunittest.impl.beans.MessageBean;
import org.camunda.bpm.engine.cdi.cdiunittest.impl.el.beans.DependentScopedBean;
import org.camunda.bpm.engine.test.Deployment;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.oneandone.ejbcdiunit.camunda.EjbCamundaUnitRule;

/**
 * @author Daniel Meyer
 */
@AdditionalClasses({ DependentScopedBean.class })
public class ElTest {

    @Inject
    RuntimeService runtimeService;
    @Inject
    MessageBean messageBean;

    @Rule
    public EjbCamundaUnitRule getEjbCamundaUnitRule() {
        return EjbCamundaUnitRule.createRuleWithAsynchronousManager(this, null);
    }

    @Test
    @Deployment
    public void testSetBeanProperty() throws Exception {
        runtimeService.startProcessInstanceByKey("setBeanProperty");
        assertEquals("Greetings from Berlin", messageBean.getMessage());
    }

    @Test
    @Ignore
    @Deployment
    public void testDependentScoped() {

        DependentScopedBean.reset();

        runtimeService.startProcessInstanceByKey("testProcess");

        // make sure the complete bean lifecycle (including invocation of @PreDestroy) was executed.
        // This ensures that the @Dependent scoped bean was properly destroyed.
        assertEquals(Arrays.asList("post-construct-invoked", "bean-invoked", "pre-destroy-invoked"), DependentScopedBean.lifecycle);
    }

}
