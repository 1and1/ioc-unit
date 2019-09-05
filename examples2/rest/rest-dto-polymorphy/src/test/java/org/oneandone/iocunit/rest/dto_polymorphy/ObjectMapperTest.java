package org.oneandone.iocunit.rest.dto_polymorphy;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oneandone.iocunit.rest.dto_polymorphy.dto.second.BDto1;
import org.oneandone.iocunit.rest.dto_polymorphy.dto.second.DtoInterface2;
import org.oneandone.iocunit.rest.dto_polymorphy.dto.typename.Dto1;
import org.oneandone.iocunit.rest.dto_polymorphy.dto.typename.Dto2;
import org.oneandone.iocunit.rest.dto_polymorphy.dto.typename.DtoInterface;
import org.oneandone.iocunit.rest.dto_polymorphy.service.Jackson2Config;
import org.oneandone.iocunit.rest.dto_polymorphy.service.RestResource;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutClasses({RestResource.class, Jackson2Config.class})
public class ObjectMapperTest {
    private static RequestSpecification spec;

    @Before
    public void before() {
        RestAssuredConfig config = config().objectMapperConfig(
                objectMapperConfig().jackson2ObjectMapperFactory((cls, charset) -> Jackson2Config.produceObjectMapper()));

        spec = new RequestSpecBuilder()
                .setConfig(config)
                .setContentType(ContentType.JSON)
                .addFilter(new ResponseLoggingFilter())//log request and response for better debugging. You can also only log if a requests fails.
                .addFilter(new RequestLoggingFilter())
                .build();

    }

    @Test
    public void canReceiveDtos() {
        given()
                .spec(spec)
                .body(new Dto2(1,"dto2"))
                .when().get("/rest/dto/1")
                .as(Dto2.class).equals(new Dto2(1,"dto2"));;
        given()
                .spec(spec)
                .when().get("/rest/dto/0")
                .as(Dto1.class).equals(new Dto1(0,"dto1"));
    }

    @Test
    public void canSendPolymorph() {
        given()
                .spec(spec)
                .body(new Dto1(0,"dto1"))
                .when().post("/rest/dto")
                .as(DtoInterface.class).equals(new Dto1(0,"dto1"));;

        given()
                .spec(spec)
                .body(new Dto2(0,"dto2"))
                .when().post("/rest/dto")
                .as(DtoInterface.class).equals(new Dto2(0,"dto2"));;

    }

    @Test
    public void canSendPolymorph2() {
        given()
                .spec(spec)
                .body(new BDto1(0, "dto1"))
                .when().post("/rest/bdto")
                .as(DtoInterface2.class).equals(new BDto1(0, "dto1"));
        ;

    }
}
