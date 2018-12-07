package cdiunit.tobetestedcode;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
