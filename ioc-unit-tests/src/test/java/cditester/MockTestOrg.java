package cditester;

import javax.enterprise.inject.Produces;

import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.oneandone.iocunit.analyzer.annotations.ExcludedClasses;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.CdiUnit2Runner;
import com.oneandone.iocunit.analyzer.annotations.ProducesAlternative;

import cditester.cdiunit.AImplementation1;
import cditester.cdiunit.AInterface;


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
