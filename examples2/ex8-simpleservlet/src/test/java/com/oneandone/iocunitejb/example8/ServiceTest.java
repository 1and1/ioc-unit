package com.oneandone.iocunitejb.example8;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.ejb.persistence.TestPersistenceFactory;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@TestClasses(TestPersistenceFactory.class)
@SutClasses({Service.class})
public class ServiceTest {

    @Inject
    Service sut;

    Server server;

    @Inject
    EntityManager entityManager;


    private TestServlet servlet;


    @Before
    public void setUp() throws Exception {
        new Thread(() -> {
            Server jetty = new Server(8080);

            WebAppContext context = new WebAppContext();

            context.setContextPath("/");

            context.setResourceBase("src/main/resources");

            jetty.setHandler(context);

            context.addServlet(TestServlet.class, "/*");


            // context.addEventListener(new org.jboss.weld.environment.servlet.Listener());


            try {
                jetty.start();
                jetty.join();
            } catch (Exception e) {
                System.out.println(e);

            }


        });

    }

    @Test
    public void canServiceReturnFive() throws Exception {

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
