package com.oneandone.iocunit.ejb.trainterceptors;

import static javax.transaction.Transactional.TxType.SUPPORTS;

import javax.annotation.Priority;
import javax.interceptor.Interceptor;
import javax.transaction.Transactional;

/**
 * @author aschoerk
 */
@Interceptor
@Transactional(SUPPORTS)
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 500)
public class TransactionalInterceptorSupports extends TransactionalInterceptorBase {

}
