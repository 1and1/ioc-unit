package com.oneandone.iocunit.ejb.trainterceptors;

import static javax.transaction.Transactional.TxType.NOT_SUPPORTED;

import javax.annotation.Priority;
import javax.interceptor.Interceptor;
import javax.transaction.Transactional;

/**
 * @author aschoerk
 */
@Interceptor
@Transactional(NOT_SUPPORTED)
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 500)
public class TransactionalInterceptorNotSupported extends TransactionalInterceptorBase {

}
