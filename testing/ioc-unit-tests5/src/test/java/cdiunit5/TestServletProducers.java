package cdiunit5;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.iocunit.IocJUnit5Extension;

@ExtendWith(IocJUnit5Extension.class)
public class TestServletProducers {
    @Inject
    private HttpServletRequest request;

    @Inject
    private HttpSession session;

    @Inject
    private ServletContext context;


    @Test
    public void testServletException() {
        Assertions.assertNotNull(request);
        Assertions.assertNotNull(session);
        Assertions.assertNotNull(context);
        ServletException.class.getClass();
    }
}
