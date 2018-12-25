package com.oneandone.ejbcdiunit5.cdibeans;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import com.oneandone.cdi.testanalyzer.annotations.SutPackages;
import com.oneandone.cdi.tester.JUnit5Extension;
import com.oneandone.cdi.testanalyzer.annotations.ProducesAlternative;
import com.oneandone.ejbcdiunit.cdibeans.AppScopedServiceBean;
import com.oneandone.ejbcdiunit.cdibeans.ServiceBeanHelperHelper;
import com.oneandone.ejbcdiunit.cdibeans.ServiceBeanHelperHelperIf;

/**
 * @author aschoerk
 */
@SutPackages({ AppScopedServiceBean.class })
@ExtendWith(JUnit5Extension.class)
public class CdiBeansAliasTest {

    @Produces
    @ProducesAlternative
    ServiceBeanHelperHelperIf helperHelperProducer() {
        return Mockito.mock(ServiceBeanHelperHelper.class);
    }

    @Inject
    AppScopedServiceBean appScopedServiceBean;


    @Test
    public void test() {
        appScopedServiceBean.getServiceBeanHelper().sumInitValues();
    }
}
