package com.oneandone.iocunit.resteasy;

import javax.enterprise.inject.Produces;
import javax.ws.rs.client.WebTarget;

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
