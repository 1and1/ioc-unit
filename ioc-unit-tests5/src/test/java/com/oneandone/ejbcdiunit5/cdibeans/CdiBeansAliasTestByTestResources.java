package com.oneandone.ejbcdiunit5.cdibeans;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.cdi.testanalyzer.annotations.SutPackages;
import com.oneandone.cdi.testanalyzer.annotations.TestClasses;
import com.oneandone.cdi.tester.JUnit5Extension;
import com.oneandone.ejbcdiunit.cdibeans.AppScopedServiceBean;

/**
 * @author aschoerk
 */
@TestClasses({ AliasTestResources.class })
@SutPackages({ AppScopedServiceBean.class })
@ExtendWith(JUnit5Extension.class)
public class CdiBeansAliasTestByTestResources {

    @Inject
    AppScopedServiceBean appScopedServiceBean;


    @Test
    public void test() {
        Assertions.assertEquals(112, appScopedServiceBean.getServiceBeanHelper().sumInitValues());
    }
}
