package cdiunittests;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import org.mockito.Mock;

import com.oneandone.iocunit.analyzer.annotations.ProducesAlternative;

import iocunit.cdiunit.tobetestedcode.HelperClassInTestResources;

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
