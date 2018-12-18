package cdiunittests;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.mockito.Mock;

import com.oneandone.cdi.tester.ProducesAlternative;

import ejbcdiunit2.first.cdiunit.tobetestedcode.HelperClassInTestResources;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class TestResources {

    @ProducesAlternative
    @Produces
    @Mock
    HelperClassInTestResources helperClassInTestResourcesMock;

    public HelperClassInTestResources getHelperClassInTestResourcesMock() {
        return helperClassInTestResourcesMock;
    }
}
