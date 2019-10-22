package com.oneandone.iocunit.ejb.trainterceptors;

import javax.interceptor.Interceptor;

/**
 * @author aschoerk
 */
@Interceptor
@EjbTransactional
public class TransactionalInterceptorEjb extends TransactionalInterceptorBase {

}
