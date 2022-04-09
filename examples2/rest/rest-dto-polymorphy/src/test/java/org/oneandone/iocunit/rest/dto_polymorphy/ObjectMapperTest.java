package org.oneandone.iocunit.rest.dto_polymorphy;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oneandone.iocunit.rest.dto_polymorphy.dto.ComplexDto;
import org.oneandone.iocunit.rest.dto_polymorphy.dto.ComplexDtoWithSetters;
import org.oneandone.iocunit.rest.dto_polymorphy.dto.abstractsuper.CDto1;
import org.oneandone.iocunit.rest.dto_polymorphy.dto.abstractsuper.CDto2;
import org.oneandone.iocunit.rest.dto_polymorphy.dto.abstractsuper.DtoSuper;
import org.oneandone.iocunit.rest.dto_polymorphy.dto.notpolymorph.NpDTo;
import org.oneandone.iocunit.rest.dto_polymorphy.dto.notpolymorph.NpDto1;
import org.oneandone.iocunit.rest.dto_polymorphy.dto.notpolymorph.NpDto2;
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
                objectMapperConfig()
                        .jackson2ObjectMapperFactory((cls, charset) ->
                                Jackson2Config.produceObjectMapper()));

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

    @Test
    public void canSendComplexPolymorph() {
        final ComplexDto complexDto = new ComplexDto(new Dto2(1, "dto2"), new BDto1(0, "dto1"));
        given()
                .spec(spec)
                .body(complexDto)
                .when().post("/rest/complexdto")
                .as(ComplexDto.class).equals(complexDto);
        ;

    }
    @Test
    public void canSendComplexPolymorph2() {
        final ComplexDtoWithSetters complexDtoWithSetters = new ComplexDtoWithSetters(new Dto2(1, "dto2"), new BDto1(0, "dto1"));

        given()
                .spec(spec)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(complexDtoWithSetters)
                .when().post("/rest/complexdtowithsetters")
                .as(ComplexDto.class).equals(complexDtoWithSetters);
        ;

    }

    @Test
    public void canSendPolymorphWithSuperClass() {
        given()
                .spec(spec)
                .body(new CDto1(0,"dto1"))
                .when().post("/rest/dtosuper")
                .as(DtoSuper.class).equals(new CDto1(0,"dto1"));;

        given()
                .spec(spec)
                .body(new CDto2(0,"dto2"))
                .when().post("/rest/dtosuper")
                .as(DtoSuper.class).equals(new CDto2(0,"dto2"));;

    }

    @Test
    public void canSendPolymorphWithContainerClass() {
        given()
                .spec(spec)
                .body(new NpDTo(new NpDto1("dto1")))
                .when().post("/rest/dtoalt")
                .equals(new NpDTo(new NpDto1("dto1")));;

        given()
                .spec(spec)
                .body(new NpDTo(new NpDto2("dto2")))
                .when().post("/rest/dtoalt")
                .equals(new NpDTo(new NpDto2("dto2")));;
    }

}
