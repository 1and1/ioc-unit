package com.oneandone.iocunitejb.persistencefactory;

import static org.hamcrest.Matchers.is;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import javax.sql.DataSource;
import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.iocunit.analyzer.annotations.EnabledAlternatives;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.ejb.persistence.PersistenceFactory;
import com.oneandone.iocunitejb.entities.TestEntity1;
import com.oneandone.iocunitejb.helpers.J2eeSimMySqlFactory;
import com.oneandone.iocunitejb.helpers.J2eeSimTest1Factory;
import com.oneandone.iocunitejb.helpers.MySqlStarter;
import com.oneandone.iocunitejb.helpers.TestResources;

/**
 * @author aschoerk
 */
@Ignore
@RunWith(IocUnitRunner.class)
@EnabledAlternatives({ J2eeSimMySqlFactory.class, TestResources.class })
@TestClasses({ MySqlStarter.class, J2eeSimTest1Factory.class })
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

            try (PreparedStatement stmt = conn.prepareStatement("insert into test_entity_1 (string_Attribute, int_Attribute) values ('sss', 114)")) {
                Assert.assertThat(stmt.executeUpdate(), is(1));
            }
        }
        userTransaction.commit();
    }

}
