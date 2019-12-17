package org.oneandone.iocunit.rest.minimal_rest_war_test;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oneandone.iocunit.rest.minimal_rest_war.restassured_1.RestResource;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutClasses(RestResource.class)
public class RestResourceTest {

    @Inject
    WebTarget webTarget;

    @Test
    public void canGetDto() {
        Response response = webTarget
                .path("/rest/dto/1")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }
    @Test
    public void canPutDto() {
        int id = 1;

        Response response = webTarget
                .path("/rest/dto/1")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .put(Entity.json("{\"id\":\"" + id + "\", \"content\": \"putRequest\" }"));
        Assert.assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());
    }

    @Test
    public void canPostDto() {
        int id = 1;

        Response response = webTarget
                .path("/rest/dto/1")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json("{\"id\":\"" + id + "\", \"content\": \"postRequest\" }"));
        Assert.assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    public void canDeleteDto() {
        int id = 1;

        Response response = webTarget
                .path("/rest/dto/1")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .delete();
        Assert.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

}
