package cdiunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jglue.cdiunit.InRequestScope;

/**
 * @author aschoerk
 */

public class RequestScopeTest {
    @Inject
    Provider<BRequestScoped> requestScoped;

    @InRequestScope
    public void testIntercepted() {
        BRequestScoped b1 = requestScoped.get();
        b1.setFoo("test"); // Force scoping
        BRequestScoped b2 = requestScoped.get();
        assertEquals(b1, b2);
    }

    public BRequestScoped get() {
        return requestScoped.get();
    }
}
