package com.oneandone.ejbcdiunit5.cdibeans;

import jakarta.enterprise.inject.Alternative;

import com.oneandone.iocunitejb.cdibeans.ServiceBeanDepScopedHelper;
import com.oneandone.iocunitejb.cdibeans.ServiceBeanHelperHelper;

/**
 * @author aschoerk
 */
@Alternative
public class HelperHelperAlias extends ServiceBeanHelperHelper {
    int aliasCounter = 0;

    ServiceBeanDepScopedHelper serviceBeanDepScopedHelper;

    @Override
    public void callInit() {
        aliasCounter++;
    }

    @Override
    public ServiceBeanDepScopedHelper getServiceBeanDepScopedHelper() {
        return serviceBeanDepScopedHelper;
    }

    public int getAliasCounter() {
        return aliasCounter;
    }
}
