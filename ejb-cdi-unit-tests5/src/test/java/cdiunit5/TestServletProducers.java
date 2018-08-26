package cdiunit5;

import com.oneandone.ejbcdiunit5.JUnit5Extension;
import junit.framework.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@ExtendWith(JUnit5Extension.class)
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
