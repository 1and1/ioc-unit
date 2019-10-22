package com.oneandone.iocunit.ejb.trainterceptors;

import static javax.transaction.Transactional.TxType.NEVER;

import javax.annotation.Priority;
import javax.interceptor.Interceptor;
import javax.transaction.Transactional;

/**
 * @author aschoerk
 */
@Interceptor
@Transactional(NEVER)
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 500)
public class TransactionalInterceptorNever extends TransactionalInterceptorBase {

}
