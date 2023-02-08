package com.oneandone.iocunitejb.example3;

import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.IocUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutPackages({AsynchronousService.class})
public class AsynchronousServiceTest {

    @Inject
    AsynchronousServiceIntf sut;

    @Produces
    RemoteServiceIntf remoteService = new RemoteServiceSimulator();

    @Test
    public void canServiceInsertEntity1Remotely() throws ExecutionException, InterruptedException {
        AsynchronousServiceIntf.CorrelationId id = sut.newRemoteEntity1(1, "test1");
        assertThat(sut.pollId(id), is(1L));
    }

    @Test
    public void canReadEntity1AfterInsertion() throws ExecutionException, InterruptedException {
        List<AsynchronousServiceIntf.CorrelationId> correlationIds = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            correlationIds.add(sut.newRemoteEntity1(i, "string: " + i));
        }
        // fetch the 6th inserted entity.
        final Long id = sut.pollId(correlationIds.get(5));
        final AsynchronousServiceIntf.CorrelationId correlationId = sut.getRemoteStringValueFor(id);
        assertThat(sut.pollString(correlationId), is(remoteService.getStringValueFor(id)));
    }

}
