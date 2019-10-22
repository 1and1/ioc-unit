package com.oneandone.iocunit.ejb.trainterceptors;

import static javax.transaction.Transactional.TxType.NOT_SUPPORTED;

import javax.interceptor.Interceptor;
import javax.transaction.Transactional;

/**
 * @author aschoerk
 */
@Interceptor
@Transactional(NOT_SUPPORTED)
public class TransactionalInterceptorNotSupported extends TransactionalInterceptorBase {

}
