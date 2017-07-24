package com.oneandone.ejbcdiunit.example6;

import static com.oneandone.ejbcdiunit.example6.AsynchronousServiceIntf.Callbacks;
import static com.oneandone.ejbcdiunit.example6.AsynchronousServiceIntf.CorrelationId;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalClasses;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.ejbcdiunit.AsynchronousManager;
import com.oneandone.ejbcdiunit.EjbUnitRunner;

/**
 * @author aschoerk
 */
@RunWith(EjbUnitRunner.class)
@AdditionalClasses({AsynchronousService.class, CallbackMdb.class, SenderMdb.class})
public class AsynchronousServiceWithCallbackMultithreadedTest {

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
        asynchronousManager.setEnqueAsynchronousCalls(true);  // asynchronous futures don't handle the call themselves
        asynchronousManager.startThread();      // a thread will periodically check for actions to be handled
    }

    @Test
    public void canServiceInsertEntity1Remotely() throws InterruptedException {
        CorrelationId correlationId = sut.newRemoteEntity1(1, "test1");
        while (idResults.size() == 0) Thread.sleep(100);
        assertThat(idResults.get(correlationId), is(1L));
    }

    @Test
    public void canReadEntity1AfterInsertion() throws InterruptedException {
        List<CorrelationId> correlationIds = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            correlationIds.add(sut.newRemoteEntity1(i, "string: " + i));
        }
        while (idResults.size() < 10) Thread.sleep(100);
        // fetch the 6th inserted entity.
        final Long id = idResults.get(correlationIds.get(5));
        final CorrelationId correlationId = sut.getRemoteStringValueFor(id);
        while (stringResults.size() == 0) Thread.sleep(100);
        assertThat(stringResults.get(correlationId), is(remoteService.getStringValueFor(id)));
    }

}
