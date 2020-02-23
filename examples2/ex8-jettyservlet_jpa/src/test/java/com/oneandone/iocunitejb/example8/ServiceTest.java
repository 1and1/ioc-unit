package com.oneandone.iocunitejb.example8;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.HandlerContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.jpa.XmlLessPersistenceFactory;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
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


    @PostConstruct
    public void setUp()  {
        Thread thread = new Thread(() -> {
            Server jetty = null;
            try {
                jetty = new Server(8081);


                WebAppContext context = new WebAppContext((HandlerContainer)null, (String)null, (SessionHandler)null,
                        new ConstraintSecurityHandler(), (ServletHandler)null, new ErrorPageErrorHandler(), 3);
                ;
                context.setContextPath("/iocunitex/*");

                context.setResourceBase("src/main/resources");

                jetty.setHandler(context);

                final ServletHolder servletHolder = new ServletHolder(this.servlet);
                context.addServlet(servletHolder, "/testservlet/*");


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

    @Test
    public void canServiceReturnFive() throws Exception {
        Thread.sleep(5000);
        URL url = new URL("http://127.0.0.1:8081/iocunitex/testservlet");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.setConnectTimeout(100000);
        con.setReadTimeout(100000);
        con.connect();
        int status = con.getResponseCode();
        String message = con.getResponseMessage();
        Assert.assertEquals(200, status);
        con.disconnect();
        Thread.sleep(5000);

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
