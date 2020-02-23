package com.oneandone.iocunit.jpa.interceptors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.iocunit.InterceptorBase;
import com.oneandone.iocunit.jpa.tra.SimulatedTransactionManager;

/**
 * @author aschoerk
 */
public class TransactionalInterceptorBase extends InterceptorBase {

    protected final Logger logger =
            LoggerFactory.getLogger(TransactionalInterceptorBase.class);
    protected SimulatedTransactionManager transactionManager = new SimulatedTransactionManager();

    static ThreadLocal<Transactional.TxType> lastTransactionAttributeType = new ThreadLocal<>();


}
