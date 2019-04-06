package com.oneandone.iocunit;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;

import org.junit.Test;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.cdi.weldstarter.CreationalContexts;
import com.oneandone.cdi.weldstarter.spi.TestExtensionService;

/**
 * @author aschoerk
 */
public class IocUnitRunner extends BlockJUnit4ClassRunner {
    private static Logger logger = LoggerFactory.getLogger(IocUnitRunner.class);
    private final Class<?> clazz;
    private final List<TestExtensionService> testExtensionServices = new ArrayList<>();
    private FrameworkMethod frameworkMethod;
    private Throwable startupException;
    IocUnitAnalyzeAndStarter analyzeAndStarter = null;

    public IocUnitRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
        analyzeAndStarter = new IocUnitAnalyzeAndStarter();

        this.clazz = clazz;
    }

    @Override
    protected Statement methodBlock(final FrameworkMethod frameworkMethodP) {
        this.frameworkMethod = frameworkMethodP;
        final Statement defaultStatement = super.methodBlock(frameworkMethodP);
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {

                if (startupException != null) {
                    if (frameworkMethodP.getAnnotation(Test.class).expected() == startupException.getClass()) {
                        return;
                    }
                    throw startupException;
                }
                System.setProperty("java.naming.factory.initial", "com.oneandone.iocunit.naming.CdiTesterContextFactory");
                InitialContext initialContext = new InitialContext();
                final BeanManager beanManager = analyzeAndStarter.get(BeanManager.class);
                initialContext.rebind("java:comp/BeanManager", beanManager);
                try (CreationalContexts creationalContexts = new CreationalContexts(beanManager)) {
                    defaultStatement.evaluate();
                } finally {
                    initialContext.close();
                    analyzeAndStarter.tearDown();
                }

            }
        };
    }


    @Override
    protected Object createTest() throws Exception {

        try {
            startupException = analyzeAndStarter.checkVersion();

            System.setProperty("java.naming.factory.initial", "com.oneandone.iocunit.naming.CdiTesterContextFactory");

            if (startupException == null) {
                try {
                    analyzeAndStarter.analyzeAndStart(clazz, frameworkMethod.getMethod());

                    analyzeAndStarter.initContexts();

                } catch (Throwable e) {
                    if(startupException == null) {
                        startupException = e;
                    }
                }
            }
        } catch (Throwable e) {
            startupException = new Exception("Unable to start weld", e);
        }

        return analyzeAndStarter.get(clazz);
    }


}

