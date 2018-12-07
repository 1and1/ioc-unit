package cdiunittests;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.oneandone.cdi.testanalyzer.annotations.SutPackages;
import com.oneandone.cdi.testanalyzer.annotations.TestClasses;
import com.oneandone.cdi.tester.CdiUnit2Runner;
import com.oneandone.cdi.tester.ProducesAlternative;

import ejbcdiunit2.first.cdiunit.tobetestedcode.HelperClass;
import ejbcdiunit2.first.cdiunit.tobetestedcode.Sut;

/**
 * @author aschoerk
 */
@RunWith(CdiUnit2Runner.class)
@TestClasses(TestResources.class)
@SutPackages({ Sut.class })
public class ProducesAltTest {

    @ProducesAlternative
    @Produces
    @Mock
    HelperClass helperMock;

    @Inject
    TestResources testResources;

    @Inject
    Sut sut;

    @Test
    public void test() {
        sut.testMethod();
        verify(helperMock, times(1)).testMethod();
    }

    @Test
    public void test2() {
        sut.testHelperClasseInTestResourcesMethod();
        verify(testResources.getHelperClassInTestResourcesMock(), times(1)).testMethod();
    }


}
