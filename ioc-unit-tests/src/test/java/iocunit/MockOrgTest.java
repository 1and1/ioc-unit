package iocunit;

import javax.enterprise.inject.Produces;

import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.ExcludedClasses;
import com.oneandone.iocunit.analyzer.annotations.ProducesAlternative;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;

import iocunit.cdiunit.AImplementation1;


/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@TestClasses({ AImplementation1.class })
@ExcludedClasses({ MockTest.class })
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
