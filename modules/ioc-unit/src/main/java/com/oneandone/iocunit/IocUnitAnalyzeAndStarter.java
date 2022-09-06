package com.oneandone.iocunit;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.cdi.weldstarter.CreationalContexts;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.cdi.weldstarter.spi.TestExtensionService;
import com.oneandone.cdi.weldstarter.spi.WeldStarter;
import com.oneandone.iocunit.analyzer.ConfigCreator;
import com.oneandone.iocunit.analyzer.InitialConfiguration;

/**
 * @author aschoerk
 */
public class IocUnitAnalyzeAndStarter {
    private static Logger logger = LoggerFactory.getLogger(IocUnitAnalyzeAndStarter.class);
    private final InitialConfiguration initialConfiguration;

    private WeldStarter weldStarter = null;
    WeldSetupClass weldSetup;
    ConfigCreator cdiConfigCreator = null;
    private CreationalContexts creationalContexts;
    InitialContext initialContext;

    public WeldStarter getWeldStarter() {
        if(weldStarter == null) {
            weldStarter = WeldSetupClass.getWeldStarter();
        }
        return weldStarter;
    }

    public <T> T get(Class<T> clazz) {
        return getWeldStarter().get(clazz);
    }

    public boolean isRunning() {
        return weldStarter != null;
    }

    private final List<TestExtensionService> testExtensionServices = new ArrayList<>();

    public IocUnitAnalyzeAndStarter(InitialConfiguration initialConfiguration) {
        this.initialConfiguration = initialConfiguration;
        if(testExtensionServices.size() == 0) {
            ServiceLoader<TestExtensionService> loader = ServiceLoader.load(TestExtensionService.class);
            final Iterator<TestExtensionService> testExtensionServiceIterator = loader.iterator();
            while (testExtensionServiceIterator.hasNext()) {
                testExtensionServices.add(testExtensionServiceIterator.next());
            }
        }
    }

    public IocUnitAnalyzeAndStarter() {
        this(new InitialConfiguration());
    }

    public void analyzeAndStart(final Class<?> clazz, Method testMethod) {
        if(cdiConfigCreator == null) {
            InitialConfiguration cfg = initialConfiguration;
            cfg.testClass = clazz;
            cfg.testMethod = testMethod;
            cfg.initialClasses.add(BeanManager.class);
            cdiConfigCreator = new ConfigCreator();
            cdiConfigCreator.create(cfg);
        }

        weldSetup = cdiConfigCreator.buildWeldSetup(testMethod);
        preStartupActions(clazz, testMethod);

        getWeldStarter().start(weldSetup);
    }

    public void preStartupActions(Class clazz, Method method) {
        if(testExtensionServices != null) {
            for (TestExtensionService te : testExtensionServices) {
                for (Class c: cdiConfigCreator.getConfiguration().getObligatory()) {
                    te.candidateToStart(c);
                }
                te.preStartupAction(weldSetup, clazz, method);
            }
        }
    }

    public void initContexts() throws Exception {
        System.setProperty("java.naming.factory.initial", "com.oneandone.iocunit.naming.CdiTesterContextFactory");
        initialContext = new InitialContext();
        final BeanManager beanManager = get(BeanManager.class);
        initialContext.rebind("java:comp/BeanManager", beanManager);

        this.creationalContexts = new CreationalContexts(beanManager);
        postStartupActions();
    }


    public void shutdownWeldIfRunning(boolean ignoreException) throws NamingException {
        if(weldStarter != null) {
            logger.trace("----> shutting down Weld");
            if(ignoreException) {
                try {
                    if(creationalContexts != null) {
                        creationalContexts.close();
                        creationalContexts = null;
                    }
                    if(initialContext != null) {
                        initialContext.close();
                        initialContext = null;
                    }
                    weldStarter.tearDown();
                } catch (Throwable thw) {
                    logger.debug("Ignored {}", thw);
                }
            }
            else {
                weldStarter.tearDown();
            }
            weldStarter = null;
        }
    }

    private static ThreadLocal<BeanManager> initBeanManager = new ThreadLocal<>();

    public static BeanManager getInitBeanManager() {
        return initBeanManager.get();
    }

    public void postStartupActions() {
        if(testExtensionServices != null) {
            initBeanManager.set(creationalContexts.getBeanManager());
            for (TestExtensionService te : testExtensionServices) {
                te.postStartupAction(creationalContexts, weldStarter);
            }
        }
    }

    public void tearDown() {
        if (creationalContexts != null) {
            creationalContexts.closeIt();
            creationalContexts = null;
        }
        initBeanManager.set(null);
        getWeldStarter().tearDown();
        weldStarter = null;
    }

    public Throwable checkVersion() {
        String version = getWeldStarter().getVersion();
        if("2.2.8 (Final)".equals(version) || "2.2.7 (Final)".equals(version)) {
            return new Exception("Weld 2.2.8 and 2.2.7 are not supported. Suggest upgrading to 2.2.9");
        }
        else {
            return null;
        }
    }

    public <T> CreationalContexts getCreationalContexts() {
        return creationalContexts;
    }
}
