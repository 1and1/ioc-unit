package com.oneandone.ejbcdiunit.cdibeans;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
