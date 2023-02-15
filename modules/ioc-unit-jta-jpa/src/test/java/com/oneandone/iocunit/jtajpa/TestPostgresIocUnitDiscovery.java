package com.oneandone.iocunit.jtajpa;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MariaDBContainer;
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

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutClasses({EntityManagerFactoryFactory.class})
@TestClasses({H2TestFactory.class, Q1Factory.class, Q2Factory.class})
@ApplicationScoped
public class TestPostgresIocUnitDiscovery extends TestProdDbBase {
    @Before
    public void beforeTestJtaJpa() {
        final DockerImageName postgresImage = DockerImageName.parse("postgres:14.6").asCompatibleSubstituteFor("postgres");
        super.setContainer(new TestContainer(new PostgreSQLContainer<>(postgresImage)));
            super.getContainer().start();
    }
}

