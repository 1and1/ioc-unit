package cdiunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import com.oneandone.iocunit.contexts.InRequestScope;

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
