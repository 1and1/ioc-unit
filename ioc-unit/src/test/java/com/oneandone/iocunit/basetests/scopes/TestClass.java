package com.oneandone.iocunit.basetests.scopes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.weld.context.bound.BoundRequestContext;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.scopes.TestRequestScope;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutClasses(RequestScopedBean.class)
public class TestClass {
    @Inject
    RequestScopedBean requestScopedBean;
    @Inject
    RequestScopedBean requestScopedBean2;

    @Inject
    BoundRequestContext boundRequestContext;


    @Test
    public void simpleTest() {
        Map<String, Object> requestDataStore = new HashMap<String, Object>();
        boundRequestContext.associate(requestDataStore);
        boundRequestContext.activate();
        assertTrue(requestScopedBean.getLocalVariable() != 0);
        assertEquals(requestScopedBean.getLocalVariable(), requestScopedBean2.getLocalVariable());
        boundRequestContext.invalidate();
        boundRequestContext.deactivate();
        boundRequestContext.dissociate(requestDataStore);
    }

    @Test
    @TestRequestScope
    public void testInterceptor() {
        assertTrue(requestScopedBean.getLocalVariable() != 0);
        assertEquals(requestScopedBean.getLocalVariable(), requestScopedBean2.getLocalVariable());
    }
}
