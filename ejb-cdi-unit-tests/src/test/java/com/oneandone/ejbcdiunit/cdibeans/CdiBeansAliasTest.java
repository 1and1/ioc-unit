package com.oneandone.ejbcdiunit.cdibeans;

import javax.enterprise.inject.New;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.cdi.testanalyzer.annotations.SutPackages;
import com.oneandone.cdi.tester.CdiUnit2Runner;
import com.oneandone.cdi.tester.ProducesAlternative;

/**
 * @author aschoerk
 */
@SutPackages({ AppScopedServiceBean.class, HelperHelperAlias.class })
@RunWith(CdiUnit2Runner.class)
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
