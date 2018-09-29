package com.oneandone.ejbcdiunit5.cdibeans;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalPackages;
import org.jglue.cdiunit.ProducesAlternative;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import com.oneandone.ejbcdiunit.cdibeans.AppScopedServiceBean;
import com.oneandone.ejbcdiunit.cdibeans.ServiceBeanHelperHelper;
import com.oneandone.ejbcdiunit.cdibeans.ServiceBeanHelperHelperIf;
import com.oneandone.ejbcdiunit5.JUnit5Extension;

/**
 * @author aschoerk
 */
@AdditionalPackages({ AppScopedServiceBean.class })
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
