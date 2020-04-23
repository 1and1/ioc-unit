package com.oneandone.iocunit.ejb.trainterceptors;

import static javax.transaction.Transactional.TxType.REQUIRES_NEW;

import javax.annotation.Priority;
import javax.interceptor.Interceptor;
import javax.transaction.Transactional;

/**
 * @author aschoerk
 */
@Interceptor
@Transactional(REQUIRES_NEW)
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 200)
public class TransactionalInterceptorRequiresNew extends TransactionalInterceptorJtaBase {

}
