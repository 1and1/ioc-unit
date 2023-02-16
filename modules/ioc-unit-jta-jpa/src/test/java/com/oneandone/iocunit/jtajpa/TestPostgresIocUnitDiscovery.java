package com.oneandone.iocunit.jtajpa;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.FrameConsumerResultCallback;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.output.ToStringConsumer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmd;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.jtajpa.helpers.H2TestFactory;
import com.oneandone.iocunit.jtajpa.helpers.Q1Factory;
import com.oneandone.iocunit.jtajpa.helpers.Q2Factory;
import com.oneandone.iocunit.jtajpa.internal.EntityManagerFactoryFactory;

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
        /*
        ImageFromDockerfile image = new ImageFromDockerfile()
                .withDockerfileFromBuilder(builder ->
                        builder
                                .from("postgres:14.6")
                                .run("/bin/sed", "-i", "-e s/#max_prepared_transactions = 0/max_prepared_transactions = 3/", "/usr/share/postgresql/postgresql.conf.sample")
                                .cmd("postgres", "-c", "fsync=off")
                                .build());
        final DockerImageName postgresImage = DockerImageName
                .parse(image.getDockerImageName())
                    .asCompatibleSubstituteFor("postgres");
        */
        final DockerImageName postgresImage = DockerImageName
                .parse("postgres:14.6")
                .asCompatibleSubstituteFor("postgres");

         PostgreSQLContainer pgcontainer = new PostgreSQLContainer<>(postgresImage);
         TestContainer testContainer = new TestContainer(pgcontainer);
        testContainer.start();

            /*
            Thread.sleep(1000);

             ExecResult result = pgcontainer.execInContainer("/bin/sed",  "-i", "-e s/#max_prepared_transactions = 0/max_prepared_transactions = 3/", "/var/lib/postgresql/data/postgresql.conf");
             if (result.getExitCode() == 0) {
                 result = pgcontainer.execInContainer("service", "postgresql", "--full-restart");
             }
             */
            return testContainer;
    }

    @Test
    @Override
    public void testWithThreeConnections() throws Exception {

    }

}

