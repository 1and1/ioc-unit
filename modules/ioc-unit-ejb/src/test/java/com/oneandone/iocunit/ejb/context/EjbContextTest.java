package com.oneandone.iocunit.ejb.context;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.MessageDrivenContext;
import javax.ejb.SessionContext;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;

/**
 * @author aschoerk
 */
@Ignore // no tests with weldstarter1 possible, because of mixing of spi version 3.0 and weld 1.1
@RunWith(IocUnitRunner.class)
@TestClasses({EjbContextTest.class})
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
