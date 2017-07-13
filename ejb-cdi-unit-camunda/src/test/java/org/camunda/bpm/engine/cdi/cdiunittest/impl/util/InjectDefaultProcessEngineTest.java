package org.camunda.bpm.engine.cdi.cdiunittest.impl.util;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.container.RuntimeContainerDelegate;
import org.camunda.bpm.engine.cdi.BusinessProcess;
import org.camunda.bpm.engine.cdi.cdiunittest.impl.beans.InjectedProcessEngineBean;
import org.camunda.bpm.engine.cdi.impl.util.ProgrammaticBeanLookup;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasspaths;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.ejbcdiunit.EjbUnitRunner;
import com.oneandone.ejbcdiunit.camunda.CdiUnitContextAssociationManager;

/**
 * @author Christopher Zell <christopher.zell@camunda.com>
 */
@RunWith(EjbUnitRunner.class)
@AdditionalClasspaths({ BusinessProcess.class, InjectedProcessEngineBean.class })
@ActivatedAlternatives({ CdiUnitContextAssociationManager.class })
public class InjectDefaultProcessEngineTest {

    private ProcessEngineRule processEngineRule = new ProcessEngineRule();

    @Rule
    public ProcessEngineRule getProcessEngineRule() {
        return processEngineRule;
    }

    @Before
    public void init() {
        if (BpmPlatform.getProcessEngineService().getDefaultProcessEngine() == null) {
            RuntimeContainerDelegate.INSTANCE.get().registerProcessEngine(processEngineRule.getProcessEngine());
        }
    }

    @After
    public void tearDownCdiProcessEngineTestCase() throws Exception {
        RuntimeContainerDelegate.INSTANCE.get().unregisterProcessEngine(processEngineRule.getProcessEngine());
    }

    @Test
    public void testProcessEngineInject() {
        // given only default engine exist

        // when TestClass is created
        InjectedProcessEngineBean testClass = ProgrammaticBeanLookup.lookup(InjectedProcessEngineBean.class);
        Assert.assertNotNull(testClass);

        // then default engine is injected
        Assert.assertEquals("default", testClass.processEngine.getName());
    }
}
