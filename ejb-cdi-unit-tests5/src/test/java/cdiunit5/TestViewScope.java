package cdiunit5;

import com.oneandone.ejbcdiunit5.JUnit5Extension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

@ExtendWith(JUnit5Extension.class)
public class TestViewScope {
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
        // check that bean can be used by more than one test: https://github.com/BrynCooke/cdi-unit/pull/124
        // (ignoring return value)
        g2ViewScoped.getRuntimeId();
    }

    @Test
    public void testTransitiveViewScoped2() {
        // check that bean can be used by more than one test: https://github.com/BrynCooke/cdi-unit/pull/124
        // (ignoring return value)
        g2ViewScoped.getRuntimeId();
    }


    @ViewScoped
    @Named
    static class ViewScopedClass {
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
    static class G2ViewScoped {
        @Inject
        private ViewScopedClass g1ViewScoped;
        private static int timesConstructed;

        public G2ViewScoped() {
            timesConstructed++;
        }

        int getRuntimeId() {
            return 1000 * timesConstructed + g1ViewScoped.getRuntimeId();
        }
    }

}

