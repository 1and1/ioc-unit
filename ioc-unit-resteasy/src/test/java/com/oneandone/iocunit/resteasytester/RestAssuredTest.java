package com.oneandone.iocunit.resteasytester;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import java.net.URISyntaxException;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutClasses({ExampleErrorMapper.class, ExampleResource.class})
public class RestAssuredTest {

    @Test
    public void testGreen() {
        given()
                .expect()
                .statusCode(200)
                .when()
                .get("/restpath/method1");
    }

    @Test
    public void canSendAndReceiveDtos() {

        given()
                .contentType("application/json")
                .body(new ExampleDto(1, "example"))
                .post("/restpath/postdto")
                .then()
                .assertThat()
                .body("id", is(1))
                .body("comment", is("example"));
    }


    @Test
    public void testExceptionMapper() throws URISyntaxException {
        given()
                .expect()
                .statusCode(400)
                .when()
                .get("/restpath/error");
    }
}
