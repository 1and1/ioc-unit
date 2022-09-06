package com.oneandone.iocunit.dbunit;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.sql.DataSource;

import org.dbunit.DatabaseUnitException;
import org.dbunit.DefaultDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.ext.h2.H2DataTypeFactory;

import com.github.mjeanroy.dbunit.core.dataset.DataSetFactory;
import com.github.mjeanroy.dbunit.core.operation.DbUnitOperation;
import com.oneandone.cdi.weldstarter.CreationalContexts;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.cdi.weldstarter.spi.TestExtensionService;
import com.oneandone.cdi.weldstarter.spi.WeldStarter;
import com.oneandone.iocunit.ejb.persistence.PersistenceFactory;
import com.oneandone.iocunit.util.Annotations;

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

    @SuppressWarnings("serial")
    public static final AnnotationLiteral<Any> ANY_LITERAL = new AnnotationLiteral<Any>() {
    };

    public Map<String, DataSource> dataSourcesForUnitNames(CreationalContexts creationalContexts, Collection<String> names) {
        HashMap res = new HashMap<String, DataSource>();
        if(names.isEmpty() || names.size() == 1 && names.iterator().next().isEmpty()) {
            try {
                DataSource datasource = (DataSource) creationalContexts.create(DataSource.class, ApplicationScoped.class);
                res.put("", datasource);
                return res;
            } catch (RuntimeException ex) {

            }
        }

        BeanManager bm = creationalContexts.getBeanManager();
        Set<Bean<?>> beans = bm.getBeans(PersistenceFactory.class, ANY_LITERAL);
        for (Bean b : beans) {
            HashSet<Bean<?>> h = new HashSet<>();
            h.add(b);
            Bean<?> b2 = bm.resolve(h);
            if(b2 != null) {
                PersistenceFactory instance = (PersistenceFactory) creationalContexts.create(b2, ApplicationScoped.class);
                if(names.contains(instance.getPersistenceUnitName())) {
                    res.put(instance.getPersistenceUnitName(), instance.produceDataSource());
                }
            }
        }
        return res;
    }

    public static class DatasetInfo {
        private String[] dataSets;
        private Boolean doOrder;
        void add(IocUnitDataSet dbUnitDataSet) {
            if (dataSets == null) {
                dataSets = dbUnitDataSet.value();
            } else {
                String[] tmp = Arrays.copyOf(dataSets, dataSets.length + dbUnitDataSet.value().length);
                int count = dataSets.length;
                for (String s: dbUnitDataSet.value()) {
                    tmp[count++] = s;
                }
                dataSets = tmp;
            }

            if (!dbUnitDataSet.order() && doOrder == null)
                    doOrder = false;
        }

        public String[] getDataSets() {
            return dataSets;
        }

        public boolean getDoOrder() {
            return doOrder == null || doOrder;
        }
    }

    Map<String, DatasetInfo> getDataSetsPerUnit() {
        Map<String, DatasetInfo> res = new HashMap<>();
        if(currentClass.get() != null) {
            IocUnitDataSet classAnnotation = Annotations.findAnnotation(currentClass.get(), IocUnitDataSet.class);
            add(res, classAnnotation);
            IocUnitDataSets classAnnotations = Annotations.findAnnotation(currentClass.get(), IocUnitDataSets.class);
            if(classAnnotations != null) {
                for (IocUnitDataSet ds : classAnnotations.value()) {
                    add(res, ds);
                }
            }
        }
        if(currentMethod.get() != null) {
            IocUnitDataSet methodAnnotation = Annotations.findAnnotation(currentMethod.get(), IocUnitDataSet.class);
            add(res, methodAnnotation);
            IocUnitDataSets methodAnnotations = Annotations.findAnnotation(currentMethod.get(), IocUnitDataSets.class);
            if(methodAnnotations != null) {
                for (IocUnitDataSet ds : methodAnnotations.value()) {
                    add(res, ds);
                }
            }
        }
        return res;
    }

    private void add(final Map<String, DatasetInfo> res, final IocUnitDataSet dsAnnotation) {
        if(dsAnnotation != null) {
            String unitName = dsAnnotation.unitName();
            if(!res.containsKey(unitName)) {
                res.put(unitName, new DatasetInfo());
            }
            DatasetInfo tmp = res.get(unitName);
            tmp.add(dsAnnotation);
        }
    }

    @Override
    public void postStartupAction(final CreationalContexts creationalContexts, final WeldStarter weldStarter) {
        Map<String, DatasetInfo> dsMap = getDataSetsPerUnit();
        Set<String> unitNames = dsMap.keySet();
        if(unitNames.size() > 0) {
            Map<String, DataSource> dataSourcesForUnitNames = dataSourcesForUnitNames(creationalContexts, unitNames);
            for (String unitName: dataSourcesForUnitNames.keySet()) {
                DataSource dataSource = dataSourcesForUnitNames.get(unitName);
                DatabaseConnection dbConnection = null;
                final DatasetInfo datasetInfo = dsMap.get(unitName);
                boolean doOrder = datasetInfo.getDoOrder();
                try {
                    IDataSet dataSet = DataSetFactory.createDataSet(datasetInfo.dataSets);
                    dbConnection = new DatabaseConnection(dataSource.getConnection());
                    dbConnection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory());
                    IDatabaseTester dbTester = new DefaultDatabaseTester(dbConnection);
                    if(doOrder) {
                        ITableFilter filter = new DatabaseSequenceFilter(dbConnection);
                        dataSet = new FilteredDataSet(filter, dataSet);
                    }
                    dbTester.setDataSet(dataSet);
                    dbTester.setSetUpOperation(DbUnitOperation.CLEAN_INSERT.getOperation());
                    dbTester.onSetup();
                    dbConnection.close();
                } catch (DatabaseUnitException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
