package com.oneandone.ejbcdiunit.ejbs;

import java.security.Principal;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;

/**
 * @author aschoerk
 */
@Startup
@Singleton
public class SingletonEJB {

    @Inject
    Logger logger;

    @Resource
    SessionContext sessionContext;

    private int publicInteger = 100;

    @PostConstruct
    public void postConstruct() {
        System.out.println("check constructions");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void callInNewTransaction() {
        System.out.println("singleton bean");
    }

    public void method1() {
        SingletonEJB res = sessionContext.getBusinessObject(SingletonEJB.class);
        res.callInNewTransaction();
        this.callInNewTransaction();
        logger.info("SingletonEJB: method1 called");
        logger.info("output public variable {}", publicInteger);

    }

    public Principal getPrincipal() {
        return sessionContext.getCallerPrincipal();
    }

}
