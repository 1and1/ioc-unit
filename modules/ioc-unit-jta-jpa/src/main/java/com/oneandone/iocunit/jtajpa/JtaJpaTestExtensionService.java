package com.oneandone.iocunit.jtajpa;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.inject.spi.Extension;
import javax.persistence.PersistenceContext;

import org.jboss.weld.transaction.spi.TransactionServices;

import com.arjuna.ats.jta.cdi.TransactionExtension;
import com.oneandone.cdi.weldstarter.WeldSetup;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.cdi.weldstarter.spi.TestExtensionService;
import com.oneandone.iocunit.jtajpa.internal.EntityManagerFactoryFactory;
import com.oneandone.iocunit.jtajpa.narayana.cdi.CDITransactionProducers;
import com.oneandone.iocunit.jtajpa.narayana.cdi.CDITransactionServices;

public class JtaJpaTestExtensionService implements TestExtensionService {
    private static ThreadLocal<Class> currentClass = new ThreadLocal<>();
    private static ThreadLocal<Method> currentMethod = new ThreadLocal<>();


    @Override
    public void preStartupAction(final WeldSetupClass weldSetup, final Class clazz, final Method method) {
        currentClass.set(clazz);
        currentMethod.set(method);
        if(weldSetup.isWeld3()) {
            if(!weldSetup.getBeanClasses().contains(CDITransactionProducers.class.getName())) {
                weldSetup.getBeanClasses().add(CDITransactionProducers.class.getName());
            }
        }
        weldSetup.addService(new WeldSetup.ServiceConfig(TransactionServices.class, new CDITransactionServices()));
    }

    @Override
    public Collection<Class<? extends Annotation>> injectAnnotations() {
        return Arrays.asList(PersistenceContext.class);
    }

    @Override
    public List<Class<?>> testClasses() {
        List<Class<?>> result = new ArrayList<Class<?>>() {
            private static final long serialVersionUID = -1661631254833065243L;

            {
                add(TransactionExtension.class);
                add(EntityManagerFactoryFactory.class);
            }
        };
        return result;
    }

    @Override
    public List<Extension> getExtensions() {
        List<Extension> result = new ArrayList<>();
        try {
            result.add(new JPAAnnotationsExtension());
        } catch (NoClassDefFoundError ex) {
            ;
        }

        return result;
    }

}
