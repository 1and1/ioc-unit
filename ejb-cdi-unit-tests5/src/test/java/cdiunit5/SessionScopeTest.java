package cdiunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;
import javax.inject.Provider;

import com.oneandone.cdi.tester.contexts.InRequestScope;
import com.oneandone.cdi.tester.contexts.InSessionScope;

/**
 * @author aschoerk
 */
public class SessionScopeTest {

    @Inject
    private Provider<CSessionScoped> sessionScoped;

    @InRequestScope
    @InSessionScope
    public void testSessionScope() {
        CSessionScoped c1 = sessionScoped.get();
        c1.setFoo("test"); // Force scoping
        CSessionScoped c2 = sessionScoped.get();
        assertEquals(c1, c2);
    }

    public CSessionScoped get() {
        return sessionScoped.get();
    }
}
