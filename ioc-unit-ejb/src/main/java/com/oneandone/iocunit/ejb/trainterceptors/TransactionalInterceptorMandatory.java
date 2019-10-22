package com.oneandone.iocunit.ejb.trainterceptors;

import static javax.transaction.Transactional.TxType.MANDATORY;

import javax.interceptor.Interceptor;
import javax.transaction.Transactional;

/**
 * @author aschoerk
 */
@Interceptor
@Transactional(MANDATORY)
public class TransactionalInterceptorMandatory extends TransactionalInterceptorBase {

}
