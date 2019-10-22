package com.oneandone.iocunit.ejb.trainterceptors;

import static javax.transaction.Transactional.TxType.NEVER;

import javax.interceptor.Interceptor;
import javax.transaction.Transactional;

/**
 * @author aschoerk
 */
@Interceptor
@Transactional(NEVER)
public class TransactionalInterceptorNever extends TransactionalInterceptorBase {

}
