package com.oneandone.iocunit.resteasy;

import jakarta.enterprise.inject.Produces;
import jakarta.ws.rs.client.WebTarget;

/**
 * @author aschoerk
 * use {@link IocUnitResteasyWebTargetBuilder instead}
 */
@Deprecated
public class TestWebTarget extends IocUnitResteasyWebTargetBuilder {
    @Produces
    WebTarget webTarget() {
        return super.webTarget();
    }
}
