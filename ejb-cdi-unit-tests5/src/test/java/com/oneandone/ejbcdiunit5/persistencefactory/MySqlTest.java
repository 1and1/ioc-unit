package com.oneandone.ejbcdiunit5.persistencefactory;

import com.oneandone.ejbcdiunit.entities.TestEntity1;
import com.oneandone.ejbcdiunit.persistence.PersistenceFactory;
import com.oneandone.ejbcdiunit5.JUnit5Extension;
import com.oneandone.ejbcdiunit5.helpers.J2eeSimMySqlFactory;
import com.oneandone.ejbcdiunit5.helpers.MySqlStarter;
import com.oneandone.ejbcdiunit5.helpers.TestResources;
import org.hamcrest.MatcherAssert;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.transaction.*;
import java.sql.*;

import static org.hamcrest.Matchers.is;

/**
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
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
                MatcherAssert.assertThat(stmt.executeUpdate(), is(1));
            }
        }
        userTransaction.commit();
    }

}
