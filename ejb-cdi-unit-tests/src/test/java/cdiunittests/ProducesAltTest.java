package cdiunittests;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.ProducesAlternative;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.oneandone.ejbcdiunit.EjbUnitRunner;

import cdiunit.tobetestedcode.HelperClass;
import cdiunit.tobetestedcode.Sut;

/**
 * @author aschoerk
 */
@RunWith(EjbUnitRunner.class)
@AdditionalClasses({ Sut.class })
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
