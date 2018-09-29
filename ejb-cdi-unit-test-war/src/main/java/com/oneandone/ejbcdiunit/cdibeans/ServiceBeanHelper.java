package com.oneandone.ejbcdiunit.cdibeans;

import javax.inject.Inject;

/**
 * @author aschoerk
 */
public class ServiceBeanHelper {


    private final ServiceBeanHelperHelperIf helper;

    @Inject
    public ServiceBeanHelper(ServiceBeanHelperHelper helper, ServiceBeanAppScopedHelper helper2) {
        System.out.println("called constructor " + this.getClass().getSimpleName());
        this.helper = helper;
        helper2.callInit();
        helper.callInit();
    }

    public void callInit() {
        helper.callInit();
    }

    public int sumInitValues() {
        return helper.getServiceBeanDepScopedHelper().initCalled;
    }
}
