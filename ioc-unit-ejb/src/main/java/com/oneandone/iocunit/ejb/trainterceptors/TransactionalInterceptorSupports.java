package com.oneandone.iocunit.ejb.trainterceptors;

import static javax.transaction.Transactional.TxType.SUPPORTS;

import javax.interceptor.Interceptor;
import javax.transaction.Transactional;

/**
 * @author aschoerk
 */
@Interceptor
@Transactional(SUPPORTS)
public class TransactionalInterceptorSupports extends TransactionalInterceptorBase {

}
