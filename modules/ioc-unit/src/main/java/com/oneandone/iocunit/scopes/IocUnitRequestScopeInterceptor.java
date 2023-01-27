package com.oneandone.iocunit.scopes;

import java.util.HashMap;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import org.jboss.weld.context.bound.BoundRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aschoerk
 */
@Interceptor
@TestRequestScope
public class IocUnitRequestScopeInterceptor {
    private static final Logger log = LoggerFactory
            .getLogger(IocUnitRequestScopeInterceptor.class);

    @Inject
    BoundRequestContext boundRequestContext;

    @AroundInvoke
    public Object around(InvocationContext ctx) throws Exception {
        Map<String, Object> requestDataStore = new HashMap<String, Object>();
        try {
            try {
                boundRequestContext.associate(requestDataStore);
                boundRequestContext.activate();
            } catch (Exception e) {
                log.error("Failed to open BoundRequestContext", e);
                throw e;
            }
            return ctx.proceed();
        } finally {
            boundRequestContext.invalidate();
            boundRequestContext.deactivate();
            boundRequestContext.dissociate(requestDataStore);
        }
    }
}
