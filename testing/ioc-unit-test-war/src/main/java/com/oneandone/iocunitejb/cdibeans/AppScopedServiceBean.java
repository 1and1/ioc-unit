package com.oneandone.iocunitejb.cdibeans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class AppScopedServiceBean {

    @Inject
    ServiceBeanHelper serviceBeanHelper;

    @PostConstruct
    void callInit() {
        System.out.println("postconstruct " + this.getClass().getSimpleName());
        serviceBeanHelper.callInit();
    }

    public ServiceBeanHelper getServiceBeanHelper() {
        return serviceBeanHelper;
    }
}
