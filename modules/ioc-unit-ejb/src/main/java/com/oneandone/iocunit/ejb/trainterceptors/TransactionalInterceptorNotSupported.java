package com.oneandone.iocunit.ejb.trainterceptors;

import static jakarta.transaction.Transactional.TxType.NOT_SUPPORTED;

import jakarta.annotation.Priority;
import jakarta.interceptor.Interceptor;
import jakarta.transaction.Transactional;

/**
 * @author aschoerk
 */
@Interceptor
@Transactional(NOT_SUPPORTED)
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 200)
public class TransactionalInterceptorNotSupported extends TransactionalInterceptorJtaBase {

}
