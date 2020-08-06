package com.oneandone.iocunit.resteasy.restassured;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;

import com.oneandone.iocunit.resteasy.DispatcherDelegate;
import com.oneandone.iocunit.resteasy.IocUnitResteasyHttpClient;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;

/**
 * @author aschoerk
 */
public class Initializer {

    public static Closeable init(DispatcherDelegate dispatcher) {
        RestAssured.reset();
        IocUnitHttpClientFactory factory = new IocUnitHttpClientFactory(dispatcher);
        RestAssured.config = new RestAssuredConfig()
                .httpClient(new HttpClientConfig()
                        .httpClientFactory(factory));
        return factory;
    }

    static class IocUnitHttpClientFactory implements HttpClientConfig.HttpClientFactory, Closeable {

        private DispatcherDelegate dispatcher;
        private List<IocUnitResteasyHttpClient> clients = new ArrayList<>();

        public IocUnitHttpClientFactory(DispatcherDelegate dispatcher) {
            this.dispatcher = dispatcher;
        }

        @Override
        public HttpClient createHttpClient() {
            IocUnitResteasyHttpClient res = new IocUnitResteasyHttpClient(dispatcher);
            clients.add(res);
            return res;
        }

        @Override
        public void close() throws IOException {
            dispatcher = null;
            clients.stream().forEach(IocUnitResteasyHttpClient::close);
        }
    }
}
