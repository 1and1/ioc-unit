package ejbcdiunit2.first;

import javax.enterprise.inject.Produces;

import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.oneandone.cdi.testanalyzer.annotations.ExcludedClasses;
import com.oneandone.cdi.testanalyzer.annotations.TestClasses;
import com.oneandone.cdi.tester.CdiUnit2Runner;
import com.oneandone.cdi.tester.ProducesAlternative;

import ejbcdiunit2.first.cdiunit.AImplementation1;
import ejbcdiunit2.first.cdiunit.AInterface;


/**
 * @author aschoerk
 */
@RunWith(CdiUnit2Runner.class)
@TestClasses({ AImplementation1.class })
@ExcludedClasses({ MockTest.class })
public class MockTestOrg extends MockTest {
    @Mock
    @ProducesAlternative
    @Produces
    private AInterface mockA;
}
