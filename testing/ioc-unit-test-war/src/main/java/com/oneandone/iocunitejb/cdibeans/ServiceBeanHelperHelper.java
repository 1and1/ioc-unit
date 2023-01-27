package com.oneandone.iocunitejb.cdibeans;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

/**
 * @author aschoerk
 */
public class ServiceBeanHelperHelper implements ServiceBeanHelperHelperIf {

    @Inject
    ServiceBeanDepScopedHelper serviceBeanDepScopedHelper;

    @PostConstruct
    public void callInit() {
        System.out.println("callInit " + this.getClass().getSimpleName());
        serviceBeanDepScopedHelper.callIndirectInit();
    }

    @Override
    public ServiceBeanDepScopedHelper getServiceBeanDepScopedHelper() {
        return serviceBeanDepScopedHelper;
    }
}
