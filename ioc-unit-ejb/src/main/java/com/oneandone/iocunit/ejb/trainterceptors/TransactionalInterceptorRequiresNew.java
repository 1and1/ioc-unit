package com.oneandone.iocunit.ejb.trainterceptors;

import static javax.transaction.Transactional.TxType.REQUIRES_NEW;

import javax.interceptor.Interceptor;
import javax.transaction.Transactional;

/**
 * @author aschoerk
 */
@Interceptor
@Transactional(REQUIRES_NEW)
public class TransactionalInterceptorRequiresNew extends TransactionalInterceptorBase {

}
