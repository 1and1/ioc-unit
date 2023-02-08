package com.oneandone.iocunitejb.example2;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunitejb.example2.useejbinject.Service;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@TestClasses({ Service.class, RemoteServiceSimulator.class })
public class ServiceWithSimulatorTest {
    @Inject
    ServiceIntf sut;

    @Inject
    RemoteServiceIntf remoteService;

    @Test
    public void canServiceInsertEntity1Remotely() {
        long id = sut.newRemoteEntity1(1, "test1");
        assertThat(id, is(1L));
    }

    @Test
    public void canReadEntity1AfterInsertion() {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ids.add(sut.newRemoteEntity1(i, "string: " + i));
        }
        // fetch the 6th inserted entity.
        assertThat(sut.getRemoteStringValueFor(ids.get(5)), is(remoteService.getStringValueFor(ids.get(5))));
    }

}
