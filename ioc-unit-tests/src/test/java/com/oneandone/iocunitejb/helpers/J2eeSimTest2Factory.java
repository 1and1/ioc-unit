package com.oneandone.iocunitejb.helpers;

import javax.enterprise.context.ApplicationScoped;

import com.oneandone.iocunit.ejb.persistence.PersistenceFactory;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class J2eeSimTest2Factory extends PersistenceFactory {
    @Override
    protected String getPersistenceUnitName() {
        return "j2eeSimDS2Test";
    }
}
