package iocunit.cdiunit.tobetestedcode;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class Sut {
    @Inject
    HelperClass helperClass;

    @Inject
    HelperClassInTestResources helperClassInTestResources;

    public void testMethod() {
        helperClass.testMethod();
    }


    public void testHelperClasseInTestResourcesMethod() {
        helperClassInTestResources.testMethod();
    }

}
