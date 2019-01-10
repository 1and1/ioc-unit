package cdiunit5;

import java.io.Serializable;

import com.oneandone.cdi.testanalyzer.annotations.TestClasses;
import com.oneandone.cdi.tester.JUnit5Extension;
import com.oneandone.cdi.tester.contexts.internal.jsf.ViewScopeExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

@ExtendWith(JUnit5Extension.class)
@TestClasses({ ViewScopeExtension.class, TestViewScope.G2ViewScoped.class })
public class TestViewScope extends BaseTest {
    @Inject
    private Provider<ViewScopedClass> viewScoped;

    @Inject
    private G2ViewScoped g2ViewScoped;


    @Test
    public void testSameBeanEachTime() {
        Assertions.assertEquals(viewScoped.get().getRuntimeId(), viewScoped.get().getRuntimeId());
    }

    @Test
    public void testTransitiveViewScoped1() {
        // check that bean can be used by more than one testIntercepted: https://github.com/BrynCooke/cdi-unit/pull/124
        // (ignoring return value)
        g2ViewScoped.getRuntimeId();
    }

    @Test
    public void testTransitiveViewScoped2() {
        // check that bean can be used by more than one testIntercepted: https://github.com/BrynCooke/cdi-unit/pull/124
        // (ignoring return value)
        g2ViewScoped.getRuntimeId();
    }


    @ViewScoped
    @Named
    static class ViewScopedClass implements Serializable {
        private static final long serialVersionUID = -1141566687058637334L;
        private static int timesConstructed;

        public ViewScopedClass() {
            timesConstructed++;
        }

        int getRuntimeId() {
            return timesConstructed;
        }
    }

    /**
     * Simple view-scoped bean that depends on another view-scoped bean implements a runtime id through the combination of a naive static variable and
     * the runtime id of its dependency..
     */
    @ViewScoped
    @Named
    static class G2ViewScoped implements Serializable {
        private static final long serialVersionUID = -1601109675904966856L;
        private static int timesConstructed;
        @Inject
        private ViewScopedClass g1ViewScoped;

        public G2ViewScoped() {
            timesConstructed++;
        }

        int getRuntimeId() {
            return 1000 * timesConstructed + g1ViewScoped.getRuntimeId();
        }
    }

}

