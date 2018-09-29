package com.oneandone.ejbcdiunit5.cdibeans;

import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.AdditionalPackages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.ejbcdiunit.cdibeans.AppScopedServiceBean;
import com.oneandone.ejbcdiunit5.JUnit5Extension;

/**
 * @author aschoerk
 */
@AdditionalClasses({ AliasTestResources.class })
@AdditionalPackages({ AppScopedServiceBean.class })
@ExtendWith(JUnit5Extension.class)
public class CdiBeansAliasTestByTestResources {

    @Inject
    AppScopedServiceBean appScopedServiceBean;


    @Test
    public void test() {
        Assertions.assertEquals(112, appScopedServiceBean.getServiceBeanHelper().sumInitValues());
    }
}
