package com.oneandone.iocunitejb.example6;

import static com.oneandone.iocunitejb.example6.AsynchronousServiceIntf.Callbacks;
import static com.oneandone.iocunitejb.example6.AsynchronousServiceIntf.CorrelationId;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.ejb.AsynchronousManager;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutClasses({ CallbackMdb.class, SenderMdb.class })
@SutPackages({ AsynchronousServiceIntf.class })
public class AsynchronousServiceWithCallbackTest {

    @Inject
    AsynchronousServiceIntf sut;

    Map<CorrelationId, Long> idResults = new HashMap<>();
    Map<CorrelationId, String> stringResults = new HashMap<>();

    @Inject
    AsynchronousManager asynchronousManager;

    @Produces
    RemoteServiceIntf remoteService = new RemoteServiceSimulator();

    @Produces
    Callbacks callbackServiceProducer() {

        return new Callbacks() {
            @Override
            public void pushId(CorrelationId correlationId, Long id) {
                idResults.put(correlationId, id);
            }

            @Override
            public void pushString(CorrelationId correlationId, String resultString) {
                stringResults.put(correlationId, resultString);
            }
        };
    }

    @Before
    public void beforeReallyAsynchronousServiceTest() {
        // asynchronousManager.setEnqueAsynchronousCalls(true); // asynchronous futures don't handle the call themselves
    }

    int waitForCount(int count, Duration maxWaitTime) {
        Instant endTime = Instant.now().plus(maxWaitTime);
        int actCount = 0;
        do {
            actCount += asynchronousManager.once();
        } while (actCount < count && Instant.now().isBefore(endTime));
        return actCount;
    }

    @Test
    public void canServiceInsertEntity1Remotely() throws ExecutionException, InterruptedException {
        CorrelationId correlationId = sut.newRemoteEntity1(1, "test1");
        assertThat(waitForCount(1, Duration.of(2, ChronoUnit.SECONDS)), is(1)); // send to remote
        assertThat(waitForCount(1, Duration.of(2, ChronoUnit.SECONDS)), is(1)); // send to remote
        assertThat(idResults.get(correlationId), is(1L));
    }

    @Test
    public void canReadEntity1AfterInsertion() throws ExecutionException, InterruptedException {
        List<CorrelationId> correlationIds = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            correlationIds.add(sut.newRemoteEntity1(i, "string: " + i));
        }
        assertThat(waitForCount(10, Duration.of(2, ChronoUnit.SECONDS)), is(10)); // sends to remote
        assertThat(waitForCount(10, Duration.of(2, ChronoUnit.SECONDS)), is(10)); // sends to callback
        // fetch the 6th inserted entity.
        final Long id = idResults.get(correlationIds.get(5));
        final CorrelationId correlationId = sut.getRemoteStringValueFor(id);
        assertThat(waitForCount(1, Duration.of(2, ChronoUnit.SECONDS)), is(1)); // send to remote
        assertThat(waitForCount(1, Duration.of(2, ChronoUnit.SECONDS)), is(1)); // send callback
        assertThat(stringResults.get(correlationId), is(remoteService.getStringValueFor(id)));
    }

}
