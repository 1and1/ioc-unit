package test;

import com.oneandone.iocunit.analyzer.annotations.ExcludedClasses;
import com.oneandone.iocunit.IocUnitRunner;
import org.junit.runner.RunWith;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
public class AbstractExcludeTest {

    @Inject
    ToInclude toInclude;
    @Produces
    ToInclude tmp = new ToInclude(); // no produces clash with excluded ToExclude

}
