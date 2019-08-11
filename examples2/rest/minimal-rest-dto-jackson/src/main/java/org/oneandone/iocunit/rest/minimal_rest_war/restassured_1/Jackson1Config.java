package org.oneandone.iocunit.rest.minimal_rest_war.restassured_1;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

/**
 * @author aschoerk
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class Jackson1Config implements ContextResolver<ObjectMapper> {
    private ObjectMapper objectMapper;

    public Jackson1Config() {

        objectMapper =  new ObjectMapper();
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
        objectMapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, true);
        objectMapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
        objectMapper.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, false);
    }

    @Override
    public ObjectMapper getContext(Class<?> arg0) {
        return objectMapper;
    }
}
