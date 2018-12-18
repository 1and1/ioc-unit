package com.oneandone.a.ejbcdiunit2.relbuilder.code;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.oneandone.ejbcdiunit.CdiTestConfig;
import com.oneandone.ejbcdiunit.cfganalyzer.TestConfigInitializer;

/**
 * @author aschoerk
 */
public class CdiUnitDeploymentCreator extends AllRelVisitor {
    private final Set<Class<?>> discoveredClasses = new HashSet<>();
    private final CdiTestConfig cdiTestConfig;

    public CdiUnitDeploymentCreator(CdiTestConfig cdiTestConfig) throws IOException, CdiRelBuilder.AnalyzerException {
        this.cdiTestConfig = cdiTestConfig;
        Set<Class<?>> classesToProcess = new HashSet<>();
        Set<Class<?>> classesToIgnore = new HashSet<>();
        TestConfigInitializer initializer = new TestConfigInitializer(cdiTestConfig, classesToProcess, classesToIgnore);
        initializer.initForAnalyzer();
        CdiRelBuilder cdiRelBuilder = new CdiRelBuilder(classesToProcess);
        Rels.RootRel rootRel = cdiRelBuilder.getRootRel();
        Object result = rootRel.accept(this, null);
    }

    @Override
    public Object visit(final Rels.RootRel rootRel, final Object p) {
        return super.visit(rootRel, p);
    }
}
