package com.oneandone.iocunit.ejb.trainterceptors;

import static javax.transaction.Transactional.TxType.MANDATORY;

import jakarta.annotation.Priority;
import jakarta.interceptor.Interceptor;
import jakarta.transaction.Transactional;

/**
 * @author aschoerk
 */
@Interceptor
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 200)
@Transactional(MANDATORY)
public class TransactionalInterceptorMandatory extends TransactionalInterceptorJtaBase {

}
