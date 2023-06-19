package com.oneandone.iocunit.jtajpa;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.jtajpa.helpers.H2TestFactory;
import com.oneandone.iocunit.jtajpa.helpers.Q1Factory;
import com.oneandone.iocunit.jtajpa.helpers.Q2Factory;
import com.oneandone.iocunit.jtajpa.internal.EntityManagerFactoryFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutClasses({EntityManagerFactoryFactory.class})
@TestClasses({H2TestFactory.class, Q1Factory.class, Q2Factory.class})
@ApplicationScoped
public class TestPostgresIocUnitDiscovery extends TestBase {
    @Produces
    @ApplicationScoped
    TestContainer postgresDBProducer() {
        final DockerImageName postgresImage = DockerImageName
                .parse("postgres:14.6")
                .asCompatibleSubstituteFor("postgres");

        PostgreSQLContainer pgcontainer = new PostgreSQLContainer<>(postgresImage);
        TestContainer testContainer = new TestContainer(pgcontainer);
        testContainer.start();
        return testContainer;
    }

    @Test
    @Override
    public void testWithThreeConnections() throws Exception {

    }

}

