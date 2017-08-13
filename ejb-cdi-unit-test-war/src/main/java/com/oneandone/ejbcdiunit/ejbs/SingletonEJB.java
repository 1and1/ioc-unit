package com.oneandone.ejbcdiunit.ejbs;

import java.security.Principal;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

/**
 * @author aschoerk
 */
@Startup
@Singleton
@ApplicationScoped
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class SingletonEJB {

    @Inject
    Logger logger;

    @Resource
    SessionContext sessionContext;

    @Inject
    SingletonEJB self;

    @EJB
    SingletonEJB selfejb;

    private int publicInteger = 100;

    @PostConstruct
    public void postConstruct() {
        System.out.println("check constructions");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void callInNewTransaction() {
        System.out.println("singleton bean");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void callInNewTransactionWithParam(int param) {
        System.out.println("singleton bean " + param);
    }

    public void methodCallUsingSessionContext() {
        SingletonEJB res = sessionContext.getBusinessObject(SingletonEJB.class);
        res.callInNewTransactionWithParam(10);
        res.callInNewTransaction();
        this.callInNewTransaction();
        logger.info("SingletonEJB: methodCallUsingSessionContext called");
        logger.info("output public variable {}", publicInteger);
    }

    public void methodCallUsingSelf() {
        self.callInNewTransaction();
        this.callInNewTransaction();
        logger.info("SingletonEJB: methodCallUsingSessionContext called");
        logger.info("output public variable {}", publicInteger);
    }

    public void methodCallUsingSelfEjb() {
        selfejb.callInNewTransaction();
        this.callInNewTransaction();
        logger.info("SingletonEJB: methodCallUsingSessionContext called");
        logger.info("output public variable {}", publicInteger);
    }

    public Principal getPrincipal() {
        return sessionContext.getCallerPrincipal();
    }

}
