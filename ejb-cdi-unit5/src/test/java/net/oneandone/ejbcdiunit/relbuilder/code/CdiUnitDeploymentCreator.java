package net.oneandone.ejbcdiunit.relbuilder.code;

import java.util.HashSet;
import java.util.Set;

import com.oneandone.ejbcdiunit.CdiTestConfig;
import com.oneandone.ejbcdiunit.cfganalyzer.MockedClassesFinder;
import com.oneandone.ejbcdiunit.cfganalyzer.TestConfigInitializer;

/**
 * @author aschoerk
 */
public class CdiUnitDeploymentCreator extends AllRelVisitor {
    private final Set<Class<?>> mockedClasses;
    private final Set<Class<?>> discoveredClasses = new HashSet<>();

    public CdiUnitDeploymentCreator(CdiTestConfig cdiTestConfig) {
        mockedClasses = new MockedClassesFinder().findMockedClassesOfTest(cdiTestConfig.getTestClass());
        Set<Class<?>> classesToProcess = new HashSet<>();
        TestConfigInitializer initializer = new TestConfigInitializer(cdiTestConfig, classesToProcess);
    }

    @Override
    public Object visit(final CdiRelBuilder.RootRel rootRel, final Object p) {
        return super.visit(rootRel, p);
    }
}
