package com.oneandone.ejbcdiunit.ex1service1entity;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

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
