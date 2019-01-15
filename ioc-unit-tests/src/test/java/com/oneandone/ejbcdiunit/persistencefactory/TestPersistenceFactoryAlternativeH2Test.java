package com.oneandone.ejbcdiunit.persistencefactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.analyzer.annotations.ExcludedClasses;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.CdiUnit2Runner;
import com.oneandone.iocunit.ejb.SessionContextFactory;
import com.oneandone.iocunit.ejb.persistence.TestPersistenceFactory;

/**
 * @author aschoerk
 */
@RunWith(CdiUnit2Runner.class)
@TestClasses({TestPersistenceFactoryAlternative.class, SessionContextFactory.class})
@ExcludedClasses({TestPersistenceFactory.class})
public class TestPersistenceFactoryAlternativeH2Test extends PersistenceFactoryTestBase {

    @Override
    protected String getStringAttributeNativeName() {
        return "string_attribute";
    }

    @Override
    protected String getIntAttributeNativeName() {
        return "int_attribute";
    }

    @Override
    public void doesFlushBeforeNativeQuery() throws Exception {

    }

    @Override
    public void checkUserTransactionAndDataSource()
            throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException, SQLException {
        if(isHibernate5()) {
            return;
        }
        super.checkUserTransactionAndDataSource();
    }

    @Test
    public void canExecuteStatementsUsingDataSource() {
        if(isHibernate5()) {
            return;
        }
        try {
            try (Connection connection = dataSource.getConnection()) {
                connection.setAutoCommit(true);
                String[] lines = new String[]{
                        "Insert into test_entity_1 (id, string_attribute, int_attribute) values (10, 's1', 1);",
                        "Insert into test_entity_1 (id, string_attribute, int_attribute) values (11, 's2', 2);",
                };
                StringBuffer sb = new StringBuffer();
                for (String line : lines) {
                    if(line.startsWith("--")) {
                        sb.delete(0, sb.length());
                    }
                    else {
                        try (Statement stmt = connection.createStatement()) {
                            if(line.trim().endsWith(";")) {
                                try {
                                    stmt.execute(sb.append(line).toString());
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                sb.delete(0, sb.length());
                            }
                            else {
                                sb.append(line);
                            }
                        }
                    }

                }
                connection.commit();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isHibernate5() {
        try {
            Class.forName("org.hibernate.boot.model.naming.PhysicalNamingStrategy");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }
}
