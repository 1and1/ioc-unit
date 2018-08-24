package com.oneandone.ejbcdiunit;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;

import org.jglue.cdiunit.AdditionalClasses;
import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.ejbcdiunit.cdiunit.CdiRunner;

/**
 * @author aschoerk
 */
@SupportEjbExtended
@AdditionalClasses({EjbUnitBeanInitializerClass.class})
public class EjbUnitRunner extends CdiRunner {

    private static Logger logger = LoggerFactory.getLogger(EjbUnitRunner.class);
    /**
     * create Runner for Test method
     * @param clazz the class of the TestMethod
     * @throws InitializationError in case of problems during initialization
     */
    public EjbUnitRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    protected Statement methodBlock(final FrameworkMethod frameworkMethodP) {
        super.frameworkMethod = frameworkMethodP;
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
                System.setProperty("java.naming.factory.initial", "com.oneandone.cdiunit.internal.naming.CdiUnitContextFactory");
                InitialContext initialContext = new InitialContext();
                final BeanManager beanManager = container.getBeanManager();
                initialContext.bind("java:comp/BeanManager", beanManager);
                try (CreationalContexts creationalContexts = new CreationalContexts(beanManager)) {
                    try {
                        Class.forName("javax.ejb.EJBContext");
                        creationalContexts.create(EjbUnitBeanInitializerClass.class, ApplicationScoped.class);
                    } catch (ClassNotFoundException e) {
                        logger.warn("Expected EJB to be present, when using EjbUnitRunner, therefore: " +
                                "could not init Startups and Jms-Objects.");
                    }

                    defaultStatement.evaluate();
                } finally {
                    initialContext.close();
                    weld.shutdown();

                }

            }
        };

    }

}
