package iocunit.ejbresource.simple;

import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.sql.DataSource;

/**
 * @author aschoerk
 */
public class SutProducesResources {
    @Produces
    @Resource(name = "datasourcename")
    DataSource dataSource1;
}
