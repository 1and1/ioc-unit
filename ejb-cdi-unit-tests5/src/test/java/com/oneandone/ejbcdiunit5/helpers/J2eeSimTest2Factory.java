package com.oneandone.ejbcdiunit5.helpers;

import com.oneandone.ejbcdiunit.persistence.PersistenceFactory;

import javax.enterprise.context.ApplicationScoped;

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
