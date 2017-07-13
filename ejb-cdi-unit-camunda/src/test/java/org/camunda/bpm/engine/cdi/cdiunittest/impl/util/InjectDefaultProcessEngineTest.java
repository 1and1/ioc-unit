package org.camunda.bpm.engine.cdi.cdiunittest.impl.util;

import com.oneandone.ejbcdiunit.EjbUnitRunner;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.container.RuntimeContainerDelegate;
import org.camunda.bpm.engine.cdi.BusinessProcess;
import org.camunda.bpm.engine.cdi.cdiunittest.CdiUnitContextAssociationManager;
import org.camunda.bpm.engine.cdi.cdiunittest.impl.beans.CreditCard;
import org.camunda.bpm.engine.cdi.cdiunittest.impl.beans.InjectedProcessEngineBean;
import org.camunda.bpm.engine.cdi.impl.util.ProgrammaticBeanLookup;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.AdditionalClasspaths;
import org.jglue.cdiunit.AdditionalPackages;
import org.junit.*;
import org.junit.runner.RunWith;

/**
 * @author Christopher Zell <christopher.zell@camunda.com>
 */
@RunWith(EjbUnitRunner.class)
@AdditionalClasspaths({BusinessProcess.class, InjectedProcessEngineBean.class})
@ActivatedAlternatives({CdiUnitContextAssociationManager.class})
public class InjectDefaultProcessEngineTest {

  @Rule
  public ProcessEngineRule getProcessEngineRule() {
    return processEngineRule;
  }

  private ProcessEngineRule processEngineRule = new ProcessEngineRule();

  @Before
  public void init() {
    if(BpmPlatform.getProcessEngineService().getDefaultProcessEngine() == null) {
      RuntimeContainerDelegate.INSTANCE.get().registerProcessEngine(processEngineRule.getProcessEngine());
    }
  }

  @After
  public void tearDownCdiProcessEngineTestCase() throws Exception {
    RuntimeContainerDelegate.INSTANCE.get().unregisterProcessEngine(processEngineRule.getProcessEngine());
  }

  @Test
  public void testProcessEngineInject() {
    //given only default engine exist

    //when TestClass is created
    InjectedProcessEngineBean testClass = ProgrammaticBeanLookup.lookup(InjectedProcessEngineBean.class);
    Assert.assertNotNull(testClass);

    //then default engine is injected
    Assert.assertEquals("default", testClass.processEngine.getName());
  }
}
