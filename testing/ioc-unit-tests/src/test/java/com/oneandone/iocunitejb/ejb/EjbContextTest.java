package com.oneandone.iocunitejb.ejb;

import jakarta.annotation.Resource;
import jakarta.ejb.EJBContext;
import jakarta.ejb.MessageDrivenContext;
import jakarta.ejb.SessionContext;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
public class EjbContextTest {

    @Resource
    EJBContext ejbContext;

    @Resource
    SessionContext sessionContext;

    @Resource
    MessageDrivenContext messageDrivenContext;

    @Test
    public void canInjectAllEjbContextTypes() {
        Assert.assertNotNull(ejbContext);
        Assert.assertNotNull(messageDrivenContext);
        Assert.assertNotNull(sessionContext);
    }
}
