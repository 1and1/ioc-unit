package com.oneandone.iocunitejb.cdibeans;

import jakarta.inject.Inject;

/**
 * @author aschoerk
 */
public class HelperHelperAlias implements ServiceBeanHelperHelperIf {
    int aliasCounter = 0;

    @Inject
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
