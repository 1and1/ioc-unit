package com.oneandone.ejbcdiunit.example3;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalClasses;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.ejbcdiunit.EjbUnitRunner;
import com.oneandone.ejbcdiunit.cdiunit.EjbQualifier;

/**
 * @author aschoerk
 */
@RunWith(EjbUnitRunner.class)
@AdditionalClasses({Service.class})
public class ServiceTest {

    @Inject
    ServiceIntf sut;

    @Produces
    @EjbQualifier
    @Default
    RemoteServiceIntf remoteService = new RemoteServiceSimulator();

    @Test
    public void canServiceInsertEntity1Remotely() throws ExecutionException, InterruptedException {
        ServiceIntf.CorrelationId id = sut.newRemoteEntity1(1, "test1");
        assertThat(sut.pollId(id), is(1L));
    }

    @Test
    public void canReadEntity1AfterInsertion() throws ExecutionException, InterruptedException {
        List<ServiceIntf.CorrelationId> correlationIds = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            correlationIds.add(sut.newRemoteEntity1(i, "string: " + i));
        }
        // fetch the 6th inserted entity.
        final Long id = sut.pollId(correlationIds.get(5));
        final ServiceIntf.CorrelationId correlationId = sut.getRemoteStringValueFor(id);
        assertThat(sut.pollString(correlationId), is(remoteService.getStringValueFor(id)));
    }

}
