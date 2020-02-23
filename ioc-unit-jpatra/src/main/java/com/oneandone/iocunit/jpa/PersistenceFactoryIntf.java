package com.oneandone.iocunit.jpa;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

/**
 * @author aschoerk
 */
public interface PersistenceFactoryIntf {

    EntityManager produceEntityManager();

    DataSource produceDataSource();
}
