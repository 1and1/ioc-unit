package com.oneandone.iocunit.ejb.trainterceptors;

import javax.annotation.Priority;
import javax.interceptor.Interceptor;

/**
 * @author aschoerk
 */
@Interceptor
@EjbTransactional
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 500)
public class TransactionalInterceptorEjb extends TransactionalInterceptorBase {

}
