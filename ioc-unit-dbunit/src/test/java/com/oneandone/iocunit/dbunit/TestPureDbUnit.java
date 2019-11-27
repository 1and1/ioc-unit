package com.oneandone.iocunit.dbunit;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.dbunit.DefaultDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.filter.ITableFilter;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.mjeanroy.dbunit.core.dataset.DataSetFactory;
import com.github.mjeanroy.dbunit.core.operation.DbUnitOperation;
import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.analyzer.annotations.TestPackages;
import com.oneandone.iocunit.entities.TestData;
import com.oneandone.iocunit.jpa.XmlLessPersistenceFactory;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@TestClasses({XmlLessPersistenceFactory.class})
@TestPackages({TestData.class})
public class TestPureDbUnit {

    @Inject
    DataSource datasource;

    @Test
    public void test() throws Exception {

        // URL testdata = this.getClass().getResource("testdata.json");
        // ResourceLoader l = ResourceLoader.find("testdata.json");
        // Resource r = l.load("testdata.json");
        String[] resources = {};
        IDataSet dataSet = DataSetFactory.createDataSet(resources);

        DatabaseConnection dbConnection = new DatabaseConnection(datasource.getConnection());
        ITableFilter filter = new DatabaseSequenceFilter(dbConnection);
        IDataSet filteredDataset = new FilteredDataSet(filter, dataSet);
        IDatabaseTester dbTester = new DefaultDatabaseTester(dbConnection);
        dbTester.setDataSet(filteredDataset);
        dbTester.setSetUpOperation(DbUnitOperation.CLEAN_INSERT.getOperation());
        dbTester.onSetup();
        dbConnection.close();
    }


}
