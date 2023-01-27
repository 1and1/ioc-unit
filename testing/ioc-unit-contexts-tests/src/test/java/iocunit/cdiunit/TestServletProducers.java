package iocunit.cdiunit;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.contexts.servlet.CdiUnitServlet;

/**
 * Taken from cdiunit-tests.
 * Originally no Qualifier @CdiUnitServlet so the SE-builtin (from >weld2) objects were used.
 * 
 */
@RunWith(IocUnitRunner.class)
public class TestServletProducers {
    @Inject
    @CdiUnitServlet
    private HttpServletRequest request;

    @Inject
    @CdiUnitServlet
    private HttpSession session;

    @Inject
    @CdiUnitServlet
    private ServletContext context;


    @Test
    public void testServletException() {
        Assert.assertNotNull(request);
        Assert.assertNotNull(session);
        Assert.assertNotNull(context);
        ServletException.class.getClass();
    }
}
