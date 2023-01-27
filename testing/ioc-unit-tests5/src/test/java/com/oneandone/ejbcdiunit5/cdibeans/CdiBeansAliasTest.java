package com.oneandone.ejbcdiunit5.cdibeans;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.IocJUnit5Extension;
import com.oneandone.iocunit.analyzer.annotations.ProducesAlternative;
import com.oneandone.iocunitejb.cdibeans.AppScopedServiceBean;
import com.oneandone.iocunitejb.cdibeans.ServiceBeanHelperHelper;
import com.oneandone.iocunitejb.cdibeans.ServiceBeanHelperHelperIf;

/**
 * @author aschoerk
 */
@SutPackages({ AppScopedServiceBean.class })
@ExtendWith(IocJUnit5Extension.class)
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
