package com.oneandone.ejbcdiunit.example2;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.ProducesAlternative;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.oneandone.ejbcdiunit.EjbUnitRunner;
import com.oneandone.ejbcdiunit.example2.uselookup.Resources;
import com.oneandone.ejbcdiunit.example2.uselookup.ServiceWithLookup;

/**
 * @author aschoerk
 */
@RunWith(EjbUnitRunner.class)
@AdditionalClasses({ServiceWithLookup.class})
public class ServiceWithAlternativeTest {
    @Inject
    ServiceIntf sut;

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
