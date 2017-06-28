package com.oneandone.ejbcdiunit.test.uselookup;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import com.oneandone.ejbcdiunit.test.RemoteServiceIntf;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class Resources {

    @Produces
    public RemoteServiceIntf lookupRemoteService() {
        return null;
    }


}
