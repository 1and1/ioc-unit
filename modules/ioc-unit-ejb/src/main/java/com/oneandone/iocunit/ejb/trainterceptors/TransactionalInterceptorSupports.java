package com.oneandone.iocunit.ejb.trainterceptors;

import static javax.transaction.Transactional.TxType.SUPPORTS;

import jakarta.annotation.Priority;
import jakarta.interceptor.Interceptor;
import jakarta.transaction.Transactional;

/**
 * @author aschoerk
 */
@Interceptor
@Transactional(SUPPORTS)
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 200)
public class TransactionalInterceptorSupports extends TransactionalInterceptorJtaBase {

}
