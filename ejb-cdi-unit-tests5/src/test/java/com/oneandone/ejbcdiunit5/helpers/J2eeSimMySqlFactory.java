package com.oneandone.ejbcdiunit5.helpers;

import com.oneandone.ejbcdiunit.persistence.PersistenceFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

/**
 * @author aschoerk
 */
@ApplicationScoped
@Alternative
public class J2eeSimMySqlFactory extends PersistenceFactory {
    @Inject
    MySqlStarter mySqlStarter; // make sure mySql is initialzed before PersistenceFactory

    @Override
    protected String getPersistenceUnitName() {
        return "mysqldb";
    }

}
