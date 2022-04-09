package org.oneandone.iocunit.rest.minimal_rest_war_test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import java.util.Date;

import javax.ws.rs.core.MediaType;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oneandone.iocunit.rest.minimal_rest_war.restassured_1.Dto;
import org.oneandone.iocunit.rest.minimal_rest_war.restassured_1.RestResource;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;

import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.mapper.ObjectMapperType;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutClasses(RestResource.class)
public class RestResourceTest {
    static <T> Matcher<Number> asDouble(final Matcher<? extends T> submatcher) {
        return new AsDouble(submatcher);
    }

    @Test
    public void stackoverflow() {
        assertThat(1.0, asDouble((closeTo(1.0,0.1))));
        assertThat(1.0, asDouble(lessThan(1.1)));

    }


    @Test
    public void canGetDto() {
        final long time = new Date().getTime();
        int id = 1;
        long normed = ((time + 3599999L) / 3600000L) * 3600000 + id;
        final Matcher<Long> timeMatcher = is(both(greaterThanOrEqualTo(normed - 3600000)).and(lessThanOrEqualTo(normed)));
        given()
                .accept(MediaType.APPLICATION_JSON)
                .when()
                .get("/rest/dto/1")
                .then()
                .statusCode(200)
                .body("number", equalTo(id + 1))
                .body("nullableNumber", equalTo(id + 2))
                .body("doubleNumber", is(both(greaterThan(3.9F)).and(lessThan(4.1F))))
                .body("name", equalTo("" + (id + 4) + ""))
                .body("timestamp", either(equalTo(normed - 3600000)).or(equalTo(normed)))
                // .body("sqlTimestamp", either(equalTo(normed - 3600000+1)).or(equalTo(normed+1)))
                // .body("character", equalTo((Character)((char)(id + 10))))
                .body("id", equalTo(id))


        ;
    }

    @Test
    public void canMirrorDto() {
        Dto toMirror = new Dto(2);
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(toMirror)
                .when()
                .post("/rest/dto")
                .then()
                .statusCode(200)
                .body("number", equalTo(toMirror.getNumber()))
                .body("nullableNumber", equalTo(toMirror.getNullableNumber()))
                .body("doubleNumber", is(both(asDouble(greaterThan(toMirror.getDoubleNumber() - 0.1)))
                        .and(asDouble(lessThan(toMirror.getDoubleNumber() + 0.1)))))
                .body("name", equalTo(toMirror.getName()))
                .body("timestamp", equalTo(toMirror.getTimestamp().getTime()))
                // .body("sqlTimestamp", equalTo(toMirror.getSqlTimestamp()))
                // .body("character", equalTo(toMirror.getCharacter().charValue()))
                .body("id", equalTo(((Number)toMirror.getId()).intValue()))


        ;
    }

    @Test
    public void canUnMarshallDtoByJackson2() {
        Dto toMirror = new Dto(2);
        Dto mirrored = given()
                .config(RestAssured.config().objectMapperConfig(new ObjectMapperConfig(ObjectMapperType.JACKSON_2)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(toMirror)
                .when()
                .post("/rest/dto")
                .then().statusCode(200).extract()
                .as(Dto.class);
        assertThat(mirrored.getId(), equalTo(toMirror.getId()));
        assertThat(mirrored.getDoubleNumber(), equalTo(toMirror.getDoubleNumber()));
    }


    @Test
    public void canUnMarshallDtoByDefaultParser() {
        Dto toMirror = new Dto(2);
        Dto mirrored = given()
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(toMirror)
                .when()
                .post("/rest/dto")
                .as(Dto.class);
        assertThat(mirrored.getId(), equalTo(toMirror.getId()));
        assertThat(mirrored.getDoubleNumber(), equalTo(toMirror.getDoubleNumber()));
    }

}
