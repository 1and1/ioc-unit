package com.oneandone.iocunit.dbunit;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.dbunit.DefaultDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.core.dataset.DataSetFactory;
import com.github.mjeanroy.dbunit.core.operation.DbUnitOperation;
import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.analyzer.annotations.TestPackages;
import com.oneandone.iocunit.entities.Aa;
import com.oneandone.iocunit.entities.TestData;
import com.oneandone.iocunit.jpa.XmlLessPersistenceFactory;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@TestClasses({XmlLessPersistenceFactory.class})
@TestPackages({TestData.class})
public class TestIt {

    @Inject
    DataSource datasource;

    @Inject
    EntityManager em;

    @Test
    public void test() throws Exception {

        // URL testdata = this.getClass().getResource("testdata.json");
        // ResourceLoader l = ResourceLoader.find("testdata.json");
        // Resource r = l.load("testdata.json");
        String[] resources = {};
        IDataSet dataSet = DataSetFactory.createDataSet(resources);
        DatabaseConnection dbConnection = new DatabaseConnection(datasource.getConnection());
        IDatabaseTester dbTester = new DefaultDatabaseTester(dbConnection);
        dbTester.setDataSet(dataSet);
        dbTester.setSetUpOperation(DbUnitOperation.CLEAN_INSERT.getOperation());
        dbTester.onSetup();
        dbConnection.close();
    }

    @Test
    @DbUnitDataSet("classpath:/testdata.json")
    public void canAnnotationTestData() {
        Aa aa = em.find(Aa.class, 1);

    }
}
