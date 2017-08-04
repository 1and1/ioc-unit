package com.oneandone.ejbcdiunit.persistencefactory;

import static org.hamcrest.Matchers.is;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.ejbcdiunit.EjbUnitRunner;
import com.oneandone.ejbcdiunit.entities.TestEntity1;
import com.oneandone.ejbcdiunit.helpers.J2eeSimMySqlFactory;
import com.oneandone.ejbcdiunit.helpers.MySqlStarter;
import com.oneandone.ejbcdiunit.helpers.TestResources;
import com.oneandone.ejbcdiunit.persistence.PersistenceFactory;

/**
 * @author aschoerk
 */
@RunWith(EjbUnitRunner.class)
@ActivatedAlternatives({ J2eeSimMySqlFactory.class, TestResources.class })
@AdditionalClasses({ MySqlStarter.class })
public class MySqlTest {

    private static Logger logger = LoggerFactory.getLogger("logger");

    @Inject
    MySqlStarter mySqlStarter;
    @Inject
    PersistenceFactory entityManagerFactory;
    @Inject
    EntityManager em;

    @Inject
    UserTransaction userTransaction;

    @Inject
    DataSource dataSource;

    @Test
    public void test() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        try {
            Object i = Class.forName("com.mysql.jdbc.Driver").newInstance();
            try (Connection conn = DriverManager.getConnection(mySqlStarter.url, "root", "")) {
                try (Statement statement = conn.createStatement()) {
                    statement.execute("create table a (a varchar(10))");
                }
            }

            // Do something with the Connection


        } catch (SQLException ex) {
            // handle any errors
            logger.error("SQLException: {} SQLState: {} VendorError: {}", ex.getMessage(), ex.getSQLState(), ex.getErrorCode());
        }
    }

    @Test
    public void test2()
            throws SQLException, SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTransaction.begin();
        TestEntity1 entity1 = new TestEntity1();
        em.persist(entity1);
        try (Connection conn = dataSource.getConnection()) {

            try (PreparedStatement stmt = conn.prepareStatement("insert into test_entity_1 (stringAttribute, intAttribute) values ('sss', 114)")) {
                Assert.assertThat(stmt.executeUpdate(), is(1));
            }
        }
        userTransaction.commit();
    }

}
