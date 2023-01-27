package com.oneandone.ejbcdiunit5.helpers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;

import com.oneandone.iocunit.jpa.XmlAwarePersistenceFactory;

/**
 * @author aschoerk
 */
@ApplicationScoped
@Alternative
public class J2eeSimMySqlFactory extends XmlAwarePersistenceFactory {
    @Inject
    MySqlStarter mySqlStarter; // make sure mySql is initialzed before PersistenceFactory

    @Override
    public String getPersistenceUnitName() {
        return "mysqldb";
    }

}
