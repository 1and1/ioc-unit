package com.oneandone.ejbcdiunit.helpers;

import javax.enterprise.context.ApplicationScoped;

import com.oneandone.cdi.tester.ejb.persistence.PersistenceFactory;

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
