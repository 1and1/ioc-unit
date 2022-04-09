package com.oneandone.iocunit.resteasytester;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpRequestPreprocessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.jboss.resteasy.mock.IocUnitResteasyDispatcher;
import com.oneandone.iocunit.restassuredtest.http.GreetingResource;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutClasses(GreetingResource.class)
public class ContentTypeTest {
    private static final String UTF_16 = "UTF-16";

    @Inject
    IocUnitResteasyDispatcher dispatcher;

    final AtomicReference<String> contentType = new AtomicReference<>();
    final List<String> contentTypes = new ArrayList<>();

    @Before
    public void beforeTest() {
        dispatcher.addHttpPreprocessor(new HttpRequestPreprocessor() {
            @Override
            public void preProcess(final HttpRequest request) {
                List<String> h = request.getHttpHeaders().getRequestHeader("Content-Type");
                contentType.set(h.get(0));
            }
        });
        dispatcher.addHttpPreprocessor(new HttpRequestPreprocessor() {
            @Override
            public void preProcess(final HttpRequest request) {
                List<String> h = request.getHttpHeaders().getRequestHeader("Content-Type");
                contentTypes.add(h.get(0));
            }
        });
    }

    @Test
    public void
    adds_default_charset_to_content_type_by_default() {
        RestAssured.given().
                contentType(ContentType.JSON).
                when().
                get("/greeting?name={name}", "Johan").
                then().
                statusCode(200);

        assertEquals(contentType.get(), "application/json");
    }
    @Test public void
    adds_specific_charset_to_content_type_by_default() {
        
        RestAssured.given().
                config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().defaultCharsetForContentType(UTF_16, ContentType.JSON))).
                contentType(ContentType.JSON).
                when().
                get("/greeting?name={name}", "Johan").
                then().
                statusCode(200);

        assertEquals(contentType.get(),"application/json; charset=" + UTF_16);
    }

    @Test public void
    doesnt_add_default_charset_to_content_type_if_charset_is_defined_explicitly() {
        
        RestAssured.given().
                contentType(ContentType.JSON.withCharset(UTF_16)).
                when().
                get("/greeting?name={name}", "Johan").
                then().
                statusCode(200);

        assertEquals(contentType.get(),"application/json; charset=UTF-16");
    }

    @Test public void
    doesnt_add_default_charset_to_content_type_if_configured_not_to_do_so() {
        
        RestAssured.given().
                config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false))).
                contentType(ContentType.JSON).
                when().
                get("/greeting?name={name}", "Johan").
                then().
                statusCode(200);

        assertEquals(contentType.get(),"application/json");
    }

    @Test public void
    doesnt_duplication_of_content_type_with_default_charset() {

        RestAssured.given().
                contentType(ContentType.JSON).
                when().
                get("/greeting?name={name}", "Johan").
                then().
                statusCode(200);

        assertEquals(1, contentTypes.size());
        assertEquals("application/json", contentTypes.get(0));
    }

    @Test public void
    doesnt_duplication_of_content_type() {

        RestAssured.given().
                config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false))).
                contentType(ContentType.JSON).
                when().
                get("/greeting?name={name}", "Johan").
                then().
                statusCode(200);

        assertEquals(1,contentTypes.size());
        assertEquals("application/json",contentTypes.get(0));
    }
    
}
