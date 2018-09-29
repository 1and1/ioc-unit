package com.oneandone.ejbcdiunit.cdibeans;

import javax.inject.Inject;

/**
 * @author aschoerk
 */
public class ServiceBeanDepScopedHelper {

    public int initCalled;

    @Inject
    ServiceBeanAppScopedHelper serviceBeanAppScopedHelper;

    public void callIndirectInit() {
        System.out.println("callIndirectInit " + this.getClass().getSimpleName());
        serviceBeanAppScopedHelper.callInit();
    }

    public int getInitCalled() {
        return initCalled;
    }

    public int getServiceBeanAppScopedHelperInit() {
        return serviceBeanAppScopedHelper.initCalled;
    }
}
