package com.oneandone.ejbcdiunit5.helpers;

import jakarta.enterprise.context.ApplicationScoped;

import com.oneandone.iocunit.jpa.XmlAwarePersistenceFactory;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class J2eeSimTest2Factory extends XmlAwarePersistenceFactory {
    @Override
    public String getPersistenceUnitName() {
        return "j2eeSimDS2Test";
    }
}
