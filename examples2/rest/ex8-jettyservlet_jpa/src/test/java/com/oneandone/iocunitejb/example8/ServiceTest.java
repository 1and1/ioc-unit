package com.oneandone.iocunitejb.example8;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.Principal;

import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.security.auth.Subject;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.eclipse.jetty.security.IdentityService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.RoleInfo;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.server.HandlerContainer;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.DecoratingListener;
import org.eclipse.jetty.webapp.WebAppContext;
import org.jboss.weld.environment.servlet.Listener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.jpa.XmlLessPersistenceFactory;

/**
 * @author aschoerk
 */
@RunWith(JUnit4.class)
@TestClasses(XmlLessPersistenceFactory.class)
@SutClasses({Service.class})
public class ServiceTest {

    @Inject
    Service sut;

    Server server;

    @Inject
    EntityManager entityManager;


    @Inject
    private TestServlet servlet;

    @Before
    public void setUp()  {
        Thread thread = new Thread(() -> {
            Server jetty = null;
            try {
                jetty = new Server(8081);


                WebAppContext context = new WebAppContext((HandlerContainer)null, (String)null, (SessionHandler)null,
                        (SecurityHandler)new SecurityHandler() {

                            @Override
                            protected RoleInfo prepareConstraintInfo(final String s, final Request request) {
                                return null;
                            }

                            @Override
                            protected boolean checkUserDataPermissions(final String s, final Request request, final Response response, final RoleInfo roleInfo) throws IOException {
                                return true;
                            }

                            @Override
                            protected boolean isAuthMandatory(final Request request, final Response response, final Object o) {
                                return false;
                            }

                            @Override
                            protected boolean checkWebResourcePermissions(final String s, final Request request, final Response response, final Object o, final UserIdentity userIdentity) throws IOException {
                                return true;
                            }
                        }, (ServletHandler)null, new ErrorPageErrorHandler(), 3);

                context.setContextPath("/iocunitex/*");

                context.setResourceBase("src/test/resources");
                jetty.setHandler(context);

                final ServletHolder servletHolder = new ServletHolder(this.servlet);
                context.addServlet(servletHolder, "/testservlet/*");
                context.addServlet(HelloWorldServlet.class, "/helloservlet/*");
                context.addEventListener(new DecoratingListener());
                context.addEventListener(new Listener());

                LoginService loginService = new LoginService() {
                    private IdentityService identityService = null;

                    @Override
                    public String getName() {
                        return null;
                    }

                    @Override
                    public UserIdentity login(final String s, final Object o, final ServletRequest servletRequest) {
                        return new UserIdentity() {
                            @Override
                            public Subject getSubject() {
                                return new Subject();
                            }

                            @Override
                            public Principal getUserPrincipal() {
                                return new Principal() {
                                    @Override
                                    public String getName() {
                                        return s;
                                    }
                                };
                            }

                            @Override
                            public boolean isUserInRole(final String role, final Scope scope) {
                                return true;
                            }
                        };
                    }

                    @Override
                    public boolean validate(final UserIdentity userIdentity) {
                        return true;
                    }

                    @Override
                    public IdentityService getIdentityService() {
                       return identityService;
                    }

                    @Override
                    public void setIdentityService(final IdentityService identityService) {

                        this.identityService = identityService;
                    }

                    @Override
                    public void logout(final UserIdentity userIdentity) {

                    }
                };
                jetty.addBean(loginService);

            } catch (Exception e) {
                System.out.println(e);
            }

            // context.addEventListener(new org.jboss.weld.environment.servlet.Listener());


            try {
                jetty.start();
                jetty.setStopAtShutdown(true);
                jetty.join();
            } catch (Exception e) {
                System.out.println(e);
            }

        });
        thread.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // @PostConstruct

    public static class HelloWorldServlet extends HttpServlet {
        private static final long serialVersionUID = 3965190802223473720L;
        @Inject BeanManager manager;


        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

            resp.setContentType("text/plain");

            resp.getWriter().append("Hello from " + manager);

        }

    }

    @Test
    public void canServiceReturnFive() throws Exception {
        Thread.sleep(5000);
        URL url = new URL("http://127.0.0.1:8081/testservlet");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        con.connect();
        int status = con.getResponseCode();
        String message = con.getResponseMessage();
        con.disconnect();
        Thread.sleep(10000);

    }

    @Test
    public void canServiceInsertEntity1() throws URISyntaxException, UnsupportedEncodingException {

    }

    private Long createEntity(int i, String s) throws URISyntaxException, UnsupportedEncodingException {
        return null;
    }

    @Test
    public void canReadEntity1AfterInsertion() throws URISyntaxException, UnsupportedEncodingException {
    }


}
