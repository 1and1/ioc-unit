package com.oneandone.iocunit.jpa.interceptors;

import static javax.transaction.Transactional.TxType.SUPPORTS;

import javax.annotation.Priority;
import javax.interceptor.Interceptor;
import javax.transaction.Transactional;

/**
 * @author aschoerk
 */
@Interceptor
@Transactional(SUPPORTS)
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 200)
public class TransactionalInterceptorSupports extends TransactionalInterceptorJtaBase {

}
