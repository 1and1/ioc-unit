package com.oneandone.iocunit.resteasy.restassured;

import org.apache.http.client.HttpClient;
import org.jboss.resteasy.core.Dispatcher;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;

/**
 * @author aschoerk
 */
public class Initializer {

    public static void init(Dispatcher dispatcher) {
        RestAssured.config = new RestAssuredConfig()
                .httpClient(new HttpClientConfig()
                        .httpClientFactory(new HttpClientConfig.HttpClientFactory() {
                            @Override
                            public HttpClient createHttpClient() {
                                return new TestHttpClient(dispatcher);
                            }
                        }));
    }
}
