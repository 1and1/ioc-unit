package com.oneandone.iocunit.ejb.trainterceptors;

import static jakarta.transaction.Transactional.TxType.REQUIRES_NEW;

import jakarta.annotation.Priority;
import jakarta.interceptor.Interceptor;
import jakarta.transaction.Transactional;

/**
 * @author aschoerk
 */
@Interceptor
@Transactional(REQUIRES_NEW)
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 200)
public class TransactionalInterceptorRequiresNew extends TransactionalInterceptorJtaBase {

}
