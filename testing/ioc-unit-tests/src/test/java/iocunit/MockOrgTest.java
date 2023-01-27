package iocunit;

import jakarta.enterprise.inject.Produces;

import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.ExcludedClasses;
import com.oneandone.iocunit.analyzer.annotations.ProducesAlternative;


/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@ExcludedClasses({MockTest.class})
public class MockOrgTest extends MockTest {
    @Mock
    @ProducesAlternative
    @Produces
    private Classes.I mockAInSub;

    @Override
    Classes.I getMockA() {
        return mockAInSub;
    }
}
