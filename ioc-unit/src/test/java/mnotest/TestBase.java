package mnotest;

import javax.enterprise.inject.Produces;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.iocunit.IocUnitRunner;

@RunWith(IocUnitRunner.class)
public abstract class TestBase {

    @Produces
    private Bean bean = new Bean();

}
