package com.oneandone.iocunit.dbunit;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;

import org.dbunit.DatabaseUnitException;
import org.dbunit.DefaultDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.ext.h2.H2DataTypeFactory;

import com.github.mjeanroy.dbunit.commons.reflection.Annotations;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.core.dataset.DataSetFactory;
import com.github.mjeanroy.dbunit.core.operation.DbUnitOperation;
import com.oneandone.cdi.weldstarter.CreationalContexts;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.cdi.weldstarter.spi.TestExtensionService;
import com.oneandone.cdi.weldstarter.spi.WeldStarter;

/**
 * @author aschoerk
 */
public class TestExtensionServices implements TestExtensionService {

    private static ThreadLocal<Class> currentClass = new ThreadLocal<>();
    private static ThreadLocal<Method> currentMethod = new ThreadLocal<>();

    @Override
    public void preStartupAction(final WeldSetupClass weldSetup, final Class clazz, final Method method) {
        currentClass.set(clazz);
        currentMethod.set(method);
    }

    @Override
    public void postStartupAction(final CreationalContexts creationalContexts, final WeldStarter weldStarter) {
        DbUnitDataSet classAnnotation = null;
        DbUnitDataSet methodAnnotation = null;
        if(currentClass.get() != null) {
            classAnnotation = Annotations.findAnnotation(currentClass.get(), DbUnitDataSet.class);
        }
        if(currentMethod.get() != null) {
            methodAnnotation = Annotations.findAnnotation(currentMethod.get(), DbUnitDataSet.class);
        }
        ArrayList<String> res = new ArrayList<>();
        if(classAnnotation != null) {
            for (String s: classAnnotation.value())
                res.add(s);
        }
        if (methodAnnotation != null) {
            if (!methodAnnotation.inherit())
                res.clear();
            for (String s: methodAnnotation.value())
                res.add(s);
        }
        if (res.size() > 0) {
            DataSource datasource = (DataSource) creationalContexts.create(DataSource.class, ApplicationScoped.class);
            DatabaseConnection dbConnection = null;
            try {
                IDataSet dataSet = DataSetFactory.createDataSet(res.toArray(new String[res.size()]));
                dbConnection = new DatabaseConnection(datasource.getConnection());
                dbConnection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory());

                IDatabaseTester dbTester = new DefaultDatabaseTester(dbConnection);
                dbTester.setDataSet(dataSet);
                dbTester.setSetUpOperation(DbUnitOperation.CLEAN_INSERT.getOperation());
                dbTester.onSetup();
                dbConnection.close();
            } catch (DatabaseUnitException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
