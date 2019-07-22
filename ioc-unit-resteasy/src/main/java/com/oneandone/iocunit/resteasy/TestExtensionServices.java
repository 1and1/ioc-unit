package com.oneandone.iocunit.resteasy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.Extension;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.cdi.weldstarter.CreationalContexts;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.cdi.weldstarter.spi.TestExtensionService;
import com.oneandone.cdi.weldstarter.spi.WeldStarter;

/**
 * @author aschoerk
 */
public class TestExtensionServices implements TestExtensionService {

    private static ThreadLocal<Set<Class>> testExtensionServiceData = new ThreadLocal<>();

    private static Logger logger = LoggerFactory.getLogger(TestExtensionServices.class);

    @Override
    public void initAnalyze() {
        if (testExtensionServiceData.get() == null)
            testExtensionServiceData.set(new HashSet<>());
    }

    @Override
    public List<Extension> getExtensions() {
        List<Extension> result = new ArrayList<>();
        try {
            if (Path.class.getName() != null)
                result.add(new JaxRsRestEasyTestExtension());
        } catch (NoClassDefFoundError ex) {
            ;
        }

        return result;
    }


    @Override
    public List<Class<?>> testClasses() {
        List<Class<?>> result = new ArrayList<>();
        result.add(RestEasyMockInit.class);
        return result;
    }

    @Override
    public void postStartupAction(final CreationalContexts creationalContexts, final WeldStarter weldStarter) {
        creationalContexts.create(RestEasyMockInit.class, ApplicationScoped.class);
    }

    /**
     * Available classes can be evaluated to be forced to be started. The evaluation also can show that some of those classes might be strong
     * candidates to be started.
     *
     * @param c the class
     * @return true if candidate is voted to be started.
     */
    @Override
    public boolean candidateToStart(final Class<?> c) {
        if (c.isAnnotationPresent(Provider.class) || c.isAnnotationPresent(Path.class)) {
            testExtensionServiceData.get().add(c);
        }
        return false;
    }

    @Override
    public void preStartupAction(WeldSetupClass weldSetup) {
        for (Class<?> c : testExtensionServiceData.get()) {
            if (!weldSetup.getBeanClasses().contains(c.getName())) {
                logger.warn("Restresource or ExceptionMapper candidate: {} found "
                            + " while scanning availables, but not in testconfiguration included.", c.getSimpleName());
            }
        }
        testExtensionServiceData.get().clear(); // show only once
    }
}
