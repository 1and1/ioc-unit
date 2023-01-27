package com.oneandone.iocunitejb.example2;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.AnalyzerFlags;
import com.oneandone.iocunit.analyzer.annotations.ProducesAlternative;
import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.ejb.EjbExtensionExtended;
import com.oneandone.iocunitejb.example2.uselookup.Resources;
import com.oneandone.iocunitejb.example2.uselookup.ServiceWithLookup;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutPackages({ServiceWithLookup.class})
@AnalyzerFlags(excludedExtensions = {EjbExtensionExtended.class})
public class ServiceWithAlternativeTest {
    @Inject
    ServiceIntf sut;

    @Mock
    @Produces
    EjbExtensionExtended ejbExtensionExtended;

    @Mock
    RemoteServiceIntf remoteService;

    @ProducesAlternative
    @Produces
    @Mock
    Resources resources;

    long idGenerator = 0;

    @Before
    public void beforeTestService() {
        when(resources.lookupRemoteService()).thenReturn(remoteService);
        when(remoteService.newEntity1(anyInt(), anyString())).thenReturn(++idGenerator);
    }

    @Test
    public void canServiceInsertEntity1Remotely() {
        long id = sut.newRemoteEntity1(1, "test1");
        assertThat(id, is(1L));
    }

    @Test
    public void canReadEntity1AfterInsertion() {
        when(remoteService.getStringValueFor(anyLong())).thenReturn("something");
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ids.add(sut.newRemoteEntity1(i, "string: " + i));
        }
        // fetch the 6th inserted entity.
        assertThat(sut.getRemoteStringValueFor(ids.get(5)), is(remoteService.getStringValueFor(ids.get(5))));
    }

}
