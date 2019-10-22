package com.oneandone.iocunit.ejb.trainterceptors;

import static javax.transaction.Transactional.TxType.REQUIRED;

import javax.annotation.Priority;
import javax.interceptor.Interceptor;
import javax.transaction.Transactional;

/**
 * @author aschoerk
 */
@Interceptor
@Transactional(REQUIRED)
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 500)
public class TransactionalInterceptorRequired extends TransactionalInterceptorBase {

}
