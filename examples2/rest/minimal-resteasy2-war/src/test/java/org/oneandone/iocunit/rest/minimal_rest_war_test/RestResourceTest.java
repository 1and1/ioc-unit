package org.oneandone.iocunit.rest.minimal_rest_war_test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import javax.ws.rs.core.MediaType;

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

    @Test
    public void canGetDto() {
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .get("/rest/dto/1")
                .then()
                .statusCode(200)
                .body("id", equalTo("1"))
                .and()
                .body("content", equalTo("dummycontent"))
        ;
    }

}
