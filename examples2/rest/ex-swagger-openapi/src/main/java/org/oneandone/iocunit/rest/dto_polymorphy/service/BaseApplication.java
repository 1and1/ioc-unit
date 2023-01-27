package org.oneandone.iocunit.rest.dto_polymorphy.service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.servlet.ServletConfig;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;

import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;

/**
 * @author aschoerk
 */
@ApplicationPath("/app")
@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(url = "https://github.com/ioc-unit", name = "example"),
                title = "ex-swagger-openapi",
                version = "1.0",
                description = ""),
        servers = {
                @Server(description = "local", url = "/ex-swagger-openapi-v1")})
public class BaseApplication extends Application {
    public BaseApplication(@Context ServletConfig servletConfig) {
        SwaggerConfiguration oasConfig = new SwaggerConfiguration().prettyPrint(true)
                .resourcePackages(Stream.of("com.oneandone.access.mobile.rest", "...").collect(Collectors.toSet()));
        try {
            new JaxrsOpenApiContextBuilder<>().servletConfig(servletConfig).application(this).openApiConfiguration(oasConfig).buildContext(true);
        } catch (OpenApiConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}

