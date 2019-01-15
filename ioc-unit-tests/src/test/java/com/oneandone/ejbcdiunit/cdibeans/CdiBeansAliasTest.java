package com.oneandone.ejbcdiunit.cdibeans;

import javax.enterprise.inject.New;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.ProducesAlternative;

/**
 * @author aschoerk
 */
@SutPackages({ AppScopedServiceBean.class, HelperHelperAlias.class })
@RunWith(IocUnitRunner.class)
public class CdiBeansAliasTest {

    @Produces
    @ProducesAlternative
    ServiceBeanHelperHelperIf helperHelperProducer(@New HelperHelperAlias helperHelperAlias) {
        return helperHelperAlias;
    }

    @Inject
    AppScopedServiceBean appScopedServiceBean;


    @Test
    public void test() {
        appScopedServiceBean.getServiceBeanHelper().sumInitValues();
    }
}
