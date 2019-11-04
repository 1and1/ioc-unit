package com.oneandone.iocunitejb.persistencefactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.ejb.XmlLessPersistenceFactory;
import com.oneandone.iocunit.ejb.persistence.JdbcSqlConverter;
import com.oneandone.iocunitejb.entities.TestEntity1;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@TestClasses({XmlLessPersistenceFactory.class, TestJdbcSqlConverter.SqlConverter.class})
@SutClasses({TestEntity1.class})
public class TestJdbcSqlConverter {
    static class SqlConverter implements JdbcSqlConverter {
        @Override
        public String convert(final String sql) {
            if (sql.startsWith("elect")) {
                return "S" + sql;
            } else {
                return sql;
            }
        }
    }

    @Inject
    DataSource dataSource;

    @Test
    public void test() throws SQLException {
        try(Connection connection = dataSource.getConnection()) {
            try (Statement s = connection.createStatement()) {
                s.execute("Insert into test_entity_1 (id, string_attribute, int_attribute) values (1, '1', 1)");
                try (ResultSet rs = s.executeQuery("elect * from test_entity_1" )) {

                }
            }
        }
    }
}
