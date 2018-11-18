package ejbcdiunit2.cdiunit;

import javax.enterprise.inject.Produces;

import org.jglue.cdiunit.ProducesAlternative;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.oneandone.cdi.testanalyzer.annotations.TestClasses;
import com.oneandone.ejbcdiunit.EjbUnitRunner;
import com.oneandone.ejbcdiunit.cdiunit.ExcludedClasses;

import cdiunit.AImplementation1;
import cdiunit.AInterface;


/**
 * @author aschoerk
 */
@RunWith(EjbUnitRunner.class)
@TestClasses({ AImplementation1.class })
@ExcludedClasses({ MockTest.class })
public class MockTestOrg extends MockTest {
    @Mock
    @ProducesAlternative
    @Produces
    private AInterface mockA;
}
