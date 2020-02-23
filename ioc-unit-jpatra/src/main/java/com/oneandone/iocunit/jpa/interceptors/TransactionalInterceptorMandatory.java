package com.oneandone.iocunit.jpa.interceptors;

import static javax.transaction.Transactional.TxType.MANDATORY;

import javax.annotation.Priority;
import javax.interceptor.Interceptor;
import javax.transaction.Transactional;

/**
 * @author aschoerk
 */
@Interceptor
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 200)
@Transactional(MANDATORY)
public class TransactionalInterceptorMandatory extends TransactionalInterceptorJtaBase {

}
