package com.oneandone.iocunitejb.example3;

import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.ejb.AsynchronousManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutPackages({AsynchronousService.class})
public class AsynchronousServiceDeterministicTest {
    @Inject
    AsynchronousServiceIntf sut;

    @Inject
    AsynchronousManager asynchronousManager;

    @Produces
    RemoteServiceIntf remoteService = new RemoteServiceSimulator();

    @Before
    public void beforeReallyAsynchronousServiceTest() {
        asynchronousManager.setEnqueAsynchronousCalls(true);
    }

    @Test
    public void canServiceInsertEntity1Remotely() throws ExecutionException, InterruptedException {
        AsynchronousServiceIntf.CorrelationId id = sut.newRemoteEntity1(1, "test1");
        assertThat(sut.pollId(id), nullValue());
        asynchronousManager.once();
        assertThat(sut.pollId(id), is(1L));
    }

    @Test
    public void canReadEntity1AfterInsertion() throws ExecutionException, InterruptedException {
        List<AsynchronousServiceIntf.CorrelationId> correlationIds = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            correlationIds.add(sut.newRemoteEntity1(i, "string: " + i));
        }
        assertThat(sut.pollId(correlationIds.get(5)), nullValue());
        asynchronousManager.once(); // simulate remote answering
        // fetch the 6th inserted entity.
        final Long id = sut.pollId(correlationIds.get(5));
        final AsynchronousServiceIntf.CorrelationId correlationId = sut.getRemoteStringValueFor(id);
        asynchronousManager.once(); // simulate remote answering
        assertThat(sut.pollString(correlationId), is(remoteService.getStringValueFor(id)));
    }

}
