package cdiunittests;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.ProducesAlternative;

import iocunit.cdiunit.tobetestedcode.HelperClass;
import iocunit.cdiunit.tobetestedcode.Sut;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
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
