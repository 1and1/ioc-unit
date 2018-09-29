package cdiunittests.alternatives;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.jglue.cdiunit.ProducesAlternative;
import org.mockito.Mock;

import cdiunit.tobetested.HelperClassInTestResources;

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
