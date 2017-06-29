package com.oneandone.ejbcdiunit.example2.uselookup;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import com.oneandone.ejbcdiunit.example2.RemoteServiceIntf;

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
