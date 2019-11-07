package com.oneandone.iocunitejb.helpers;

import javax.enterprise.context.ApplicationScoped;

import com.oneandone.iocunit.jpa.XmlAwarePersistenceFactory;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class J2eeSimTest2Factory extends XmlAwarePersistenceFactory {
    @Override
    protected String getPersistenceUnitName() {
        return "j2eeSimDS2Test";
    }
}
