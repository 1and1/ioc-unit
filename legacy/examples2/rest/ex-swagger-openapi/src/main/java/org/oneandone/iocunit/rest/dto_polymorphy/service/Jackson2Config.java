package org.oneandone.iocunit.rest.dto_polymorphy.service;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author aschoerk
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class Jackson2Config implements ContextResolver<ObjectMapper> {
    public static ObjectMapper produceObjectMapper() {
        return new ObjectMapper()
                // use ISO 8601 as for date and time
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                // allow properties to be added or removed (like in JAVA serialization)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
                // pretty print
                .enable(SerializationFeature.INDENT_OUTPUT)
                // leave out empty values
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)

                // do not
                .disable(SerializationFeature.WRAP_ROOT_VALUE)
                .disable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
                .disable(DeserializationFeature.UNWRAP_ROOT_VALUE)

                // but accept clients who do
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .enableDefaultTyping();
    }

    private ObjectMapper objectMapper;

    public Jackson2Config() {

        objectMapper =  produceObjectMapper();
    }

    @Override
    public ObjectMapper getContext(Class<?> arg0) {
        return objectMapper;
    }
}
