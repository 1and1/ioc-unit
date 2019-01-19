package com.oneandone.iocunitejb.cdibeans;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.IocUnitRunner;

/**
 * @author aschoerk
 */
@SutPackages({ AppScopedServiceBean.class })
@RunWith(IocUnitRunner.class)
public class CdiBeansTest {

    @Inject
    AppScopedServiceBean appScopedServiceBean;


    @Test
    public void test() {
        appScopedServiceBean.getServiceBeanHelper().sumInitValues();
    }
}
