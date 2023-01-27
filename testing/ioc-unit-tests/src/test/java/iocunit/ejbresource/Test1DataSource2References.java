package iocunit.ejbresource;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.sql.DataSource;

import com.oneandone.iocunit.resource.ResourceQualifier;

/**
 * @author aschoerk
 */
public class Test1DataSource2References {
    @Inject
    DataSource dataSource;

    @Produces
    @ResourceQualifier(name = "datasourcename")
    DataSource produceQualifiedDatasourceName() {
        return dataSource;
    }
}
