package org.oneandone.iocunit.rest.minimal_rest_war_test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oneandone.iocunit.rest.minimal_rest_war.restassured_1.Dto;
import org.oneandone.iocunit.rest.minimal_rest_war.restassured_1.Jackson2Config;
import org.oneandone.iocunit.rest.minimal_rest_war.restassured_1.RestResource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;

import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.path.json.mapper.factory.Jackson2ObjectMapperFactory;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutClasses({RestResource.class, Jackson2Config.class,})
public class ObjectMapperTest {
    static <T> Matcher<Number> asDouble(final Matcher<? extends T> submatcher) {
        return new AsDouble(submatcher);
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
                .body("id", equalTo(((Number) toMirror.getId()).intValue()))


        ;
    }


    @Test
    public void canAcceptJsonString() {
        int id = 1;
        Dto resultDto = given()
                .config(RestAssured.config().objectMapperConfig(new ObjectMapperConfig() {
                    public Jackson2ObjectMapperFactory jackson2ObjectMapperFactory() {
                        return new Jackson2ObjectMapperFactory() {

                            @Override
                            public ObjectMapper create(final Type cls, final String charset) {
                                return new Jackson2Config().getContext(null);
                            }
                        };
                    }
                })).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON).
                        body("{\n"
                             + "  \"id\": 1,\n"
                             + "  \"number\": 2,\n"
                             + "  \"nullableNumber\": 3,\n"
                             + "  \"doubleNumber\": 4.0,\n"
                             + "  \"name\": \"5\",\n"
                             + "  \"timestamp\": \"2019-08-08T16:00:00.001+0000\",\n"
                             + "  \"sqlTimestamp\": \"2019-08-08\",\n"
                             + "  \"character\": \"\\u0006\",\n"
                             + "  \"integers\": [0,1,2,3],\n"
                             + "  \"smallDtos\": [{\n"
                             + "    \"id\": 1,\n"
                             + "    \"value\": \"1\"\n"
                             + "  }, {\n"
                             + "    \"id\": 1,\n"
                             + "    \"value\": \"1\",\n"
                             + "    \"xxx\": \"2222\"\n"
                             + "  }]\n"
                             + "}").
                        when().
                        post("/rest/dto").
                        as(Dto.class);

    }


}
