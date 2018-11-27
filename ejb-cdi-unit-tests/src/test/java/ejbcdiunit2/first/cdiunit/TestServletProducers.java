package ejbcdiunit2.first.cdiunit;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.ejbcdiunit2.runner.EjbCdiUnit2Runner;

@RunWith(EjbCdiUnit2Runner.class)
public class TestServletProducers {
    @Inject
    private HttpServletRequest request;

    @Inject
    private HttpSession session;

    @Inject
    private ServletContext context;


    @Test
    public void testServletException() {
        Assert.assertNotNull(request);
        Assert.assertNotNull(session);
        Assert.assertNotNull(context);
        ServletException.class.getClass();
    }
}
