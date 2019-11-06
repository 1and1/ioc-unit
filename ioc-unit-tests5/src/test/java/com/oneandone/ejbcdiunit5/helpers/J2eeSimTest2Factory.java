package com.oneandone.ejbcdiunit5.helpers;

import javax.enterprise.context.ApplicationScoped;

import com.oneandone.iocunit.ejb.persistence.XmlAwarePersistenceFactory;

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
