package org.oneandone.iocunit.rest.dto_polymorphy;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.ws.rs.core.Application;

import org.jboss.resteasy.core.Dispatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oneandone.iocunit.rest.dto_polymorphy.service.RestResource;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiContext;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutClasses(RestResource.class)
public class RestResourceTest {
    private static RequestSpecification spec;

    @Inject
    Dispatcher dispatcher;

    @Before
    public void before() {
        SwaggerConfiguration oasConfig = new SwaggerConfiguration().prettyPrint(true)
                .resourcePackages(Stream.of("org.oneandone.iocunit.rest.dto_polymorphy", "...").collect(Collectors.toSet()));
        try {
            OpenApiContext context = new JaxrsOpenApiContextBuilder<>()
                    // .servletConfig((ServletConfig)dispatcher)
                    .application(new Application()).openApiConfiguration(oasConfig).buildContext(true);
        } catch (OpenApiConfigurationException e) {
            throw new RuntimeException(e);
        }
        spec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addFilter(new ResponseLoggingFilter()) //log request and response for better debugging. You can also only log if a requests fails.
                .addFilter(new RequestLoggingFilter())
                .build();
    }

    @Test
    public void canGetDto1() {
        given()
                .spec(spec)
                .when()
                .get("/rest/dto/0")
                .then()
                .statusCode(200)
                .body("id", equalTo(0))
                .and()
                .body("name", equalTo("dto1"))
        ;
    }
    @Test
    public void canGetDto2() {
        given()
                .spec(spec)
                .when()
                .get("/rest/dto/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .and()
                .body("dto2Name", equalTo("dto2"))
        ;
    }

    @Test
    public void canGetDto2Object() {
        given()
                .spec(spec)
                .when()
                .get("/rest/dto/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .and()
                .body("dto2Name", equalTo("dto2"))
        ;
    }



}
