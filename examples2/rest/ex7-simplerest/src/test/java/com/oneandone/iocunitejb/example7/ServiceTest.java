package com.oneandone.iocunitejb.example7;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.jboss.resteasy.mock.IocUnitMockDispatcherFactory;
import com.oneandone.iocunit.jboss.resteasy.mock.IocUnitResteasyDispatcher;
import com.oneandone.iocunit.jpa.XmlLessPersistenceFactory;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@TestClasses(XmlLessPersistenceFactory.class)
@SutClasses({Service.class})
public class ServiceTest {

    @Inject
    Service sut;

    @Inject
    EntityManager entityManager;

    private IocUnitResteasyDispatcher dispatcher;

    @Before
    public void setUp() {
        dispatcher = IocUnitMockDispatcherFactory.createDispatcher();
        dispatcher.getRegistry().addSingletonResource(sut);
    }

    @Test
    public void canServiceReturnFive() throws URISyntaxException, UnsupportedEncodingException {
        MockHttpRequest request = MockHttpRequest.get("/simplerest/numbers/5");
        MockHttpResponse response = new MockHttpResponse();
        dispatcher.invoke(request, response);
        assertEquals(200, response.getStatus());
        assertThat(response.getContentAsString(), is("5"));
    }

    @Test
    public void canServiceInsertEntity1() throws URISyntaxException, UnsupportedEncodingException {
        Long id = createEntity(1, "test1");
        assertThat(1L, is(id));
        List<Entity1> resultList = entityManager.createQuery("Select e from Entity1 e", Entity1.class).getResultList();
        assertThat(resultList.size(), is(1));
        Entity1 entity1 = resultList.iterator().next();
        assertThat(entity1.getIntValue(), is(1));
        assertThat(entity1.getStringValue(), is("test1"));
    }

    private Long createEntity(int i, String s) throws URISyntaxException, UnsupportedEncodingException {
        MockHttpRequest request = MockHttpRequest.post("/simplerest/entities/entity1?intvalue=" + i + "&stringvalue="
                                                       + URLEncoder.encode(s, "UTF-8"));
        MockHttpResponse response = new MockHttpResponse();
        dispatcher.invoke(request, response);
        assertEquals(200, response.getStatus());
        return Long.parseLong(response.getContentAsString());
    }

    @Test
    public void canReadEntity1AfterInsertion() throws URISyntaxException, UnsupportedEncodingException {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Long id = createEntity(i, "string: " + i);
            ids.add(id);
        }

        MockHttpRequest request = MockHttpRequest.get("/simplerest/entities/entity1/string?id=" + ids.get(5));
        MockHttpResponse response = new MockHttpResponse();
        dispatcher.invoke(request, response);
        // fetch the 6th inserted entity.
        assertThat(response.getContentAsString(), is("string: 5"));
    }


}
