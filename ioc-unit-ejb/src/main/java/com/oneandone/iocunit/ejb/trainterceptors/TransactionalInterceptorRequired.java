package com.oneandone.iocunit.ejb.trainterceptors;

import static javax.transaction.Transactional.TxType.REQUIRED;

import javax.interceptor.Interceptor;
import javax.transaction.Transactional;

/**
 * @author aschoerk
 */
@Interceptor
@Transactional(REQUIRED)
public class TransactionalInterceptorRequired extends TransactionalInterceptorBase {

}
