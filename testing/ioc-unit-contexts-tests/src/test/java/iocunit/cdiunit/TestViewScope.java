package iocunit.cdiunit;

import java.io.Serializable;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Provider;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.contexts.internal.jsf.ViewScopeExtension;

@RunWith(IocUnitRunner.class)
@TestClasses({ ViewScopeExtension.class, TestViewScope.G2ViewScoped.class })
public class TestViewScope extends BaseTest {
    @Inject
    private Provider<ViewScopedClass> viewScoped;

    @Inject
    private G2ViewScoped g2ViewScoped;


    @Test
    public void testSameBeanEachTime() {
        Assert.assertEquals(viewScoped.get().getRuntimeId(), viewScoped.get().getRuntimeId());
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
    static class ViewScopedClass implements Serializable {
        private static final long serialVersionUID = -4340106158381549495L;
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

        private static final long serialVersionUID = 4772024614157652868L;
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

