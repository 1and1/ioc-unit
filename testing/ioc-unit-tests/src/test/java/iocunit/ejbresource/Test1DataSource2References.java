package iocunit.ejbresource;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.sql.DataSource;

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
