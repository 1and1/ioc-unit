package com.oneandone.ejbcdiunit.cdibeans;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.cdi.tester.CdiUnit2Runner;

/**
 * @author aschoerk
 */
@SutPackages({ AppScopedServiceBean.class })
@RunWith(CdiUnit2Runner.class)
public class CdiBeansTest {

    @Inject
    AppScopedServiceBean appScopedServiceBean;


    @Test
    public void test() {
        appScopedServiceBean.getServiceBeanHelper().sumInitValues();
    }
}
