package com.oneandone.iocunitejb.helpers;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

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
