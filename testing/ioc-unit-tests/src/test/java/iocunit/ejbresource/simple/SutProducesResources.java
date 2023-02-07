package iocunit.ejbresource.simple;

import jakarta.annotation.Resource;
import jakarta.enterprise.inject.Produces;
import javax.sql.DataSource;

/**
 * @author aschoerk
 */
public class SutProducesResources {
    @Produces
    @Resource(name = "datasourcename")
    DataSource dataSource1;
}
