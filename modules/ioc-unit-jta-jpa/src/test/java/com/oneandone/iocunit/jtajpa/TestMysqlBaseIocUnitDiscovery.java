package com.oneandone.iocunit.jtajpa;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MariaDBContainer;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.jtajpa.helpers.H2TestFactory;
import com.oneandone.iocunit.jtajpa.helpers.Q1Factory;
import com.oneandone.iocunit.jtajpa.helpers.Q2Factory;
import com.oneandone.iocunit.jtajpa.internal.EntityManagerFactoryFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@RunWith(IocUnitRunner.class)
@SutClasses({ EntityManagerFactoryFactory.class })
@TestClasses({ H2TestFactory.class, Q1Factory.class, Q2Factory.class })
@ApplicationScoped
public class TestMysqlBaseIocUnitDiscovery extends TestBase {

    @Produces
    TestContainer mariaDbProducer() {
        TestContainer container = new TestContainer(new MariaDBContainer<>("mariadb"));
        container.start();
        return container;
    }

    @Test
    @Override
    public void testWithThreeConnections() throws Exception {
        super.testWithThreeConnections();
    }

}

