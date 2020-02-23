package com.oneandone.iocunit.jpa;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;
import javax.enterprise.inject.spi.Extension;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.PersistenceContext;

import org.jboss.weld.bootstrap.spi.Metadata;
import org.jboss.weld.transaction.spi.TransactionServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.cdi.weldstarter.WeldSetup;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.cdi.weldstarter.spi.TestExtensionService;
import com.oneandone.iocunit.ejb.EjbExtensionExtended;
import com.oneandone.iocunit.jpa.interceptors.TransactionalInterceptorMandatory;
import com.oneandone.iocunit.jpa.interceptors.TransactionalInterceptorNever;
import com.oneandone.iocunit.jpa.interceptors.TransactionalInterceptorNotSupported;
import com.oneandone.iocunit.jpa.interceptors.TransactionalInterceptorRequired;
import com.oneandone.iocunit.jpa.interceptors.TransactionalInterceptorRequiresNew;
import com.oneandone.iocunit.jpa.interceptors.TransactionalInterceptorSupports;
import com.oneandone.iocunit.jpa.jpa.JpaExtension;
import com.oneandone.iocunit.jpa.jpa.PersistenceFactory;
import com.oneandone.iocunit.jpa.jpa.PersistenceFactoryResources;
import com.oneandone.iocunit.jpa.tra.SimulatedTransactionManager;
import com.oneandone.iocunit.jpa.tra.SimulatedUserTransaction;

/**
 * @author aschoerk
 */
public class JpaTraTestExtensionService implements TestExtensionService {
    private static Logger logger = LoggerFactory.getLogger("JpaTestExtensionService");

    private boolean foundPersistenceFactory = false;

    static class JpaTestExtensionServiceData {
        HashSet<Class<?>> candidatesToStart = new HashSet<>();
    }

    private static ThreadLocal<JpaTraTestExtensionService.JpaTestExtensionServiceData> jpaTestExtensionServiceData = new ThreadLocal<>();

    @Override
    public void initAnalyze() {
        if(jpaTestExtensionServiceData.get() == null) {
            jpaTestExtensionServiceData.set(new JpaTraTestExtensionService.JpaTestExtensionServiceData());
        }
    }

    @Override
    public List<Class<?>> testClasses() {
        List<Class<?>> result = new ArrayList<Class<?>>() {
            private static final long serialVersionUID = -1661631254833065243L;

            {
                // add(WeldSEBeanRegistrant.class);
                add(TransactionalInterceptorRequired.class);
                add(TransactionalInterceptorRequiresNew.class);
                add(TransactionalInterceptorMandatory.class);
                add(TransactionalInterceptorNever.class);
                add(TransactionalInterceptorNotSupported.class);
                add(TransactionalInterceptorSupports.class);
                add(SimulatedTransactionManager.class);
                add(PersistenceFactoryResources.class);
                add(JpaExtension.class);
            }
        };
        return result;
    }

    @Override
    public boolean candidateToStart(Class<?> c) {
        if(c.getAnnotation(Entity.class) != null
           || c.getAnnotation(MappedSuperclass.class) != null) {
            jpaTestExtensionServiceData.get().candidatesToStart.add(c);
        }

        if(PersistenceFactory.class.isAssignableFrom(c)) {
            this.foundPersistenceFactory = true;
        }
        return c.getAnnotation(Entity.class) != null;
    }

    @Override
    public Collection<Class<? extends Annotation>> injectAnnotations() {
        return Arrays.asList(Resource.class, PersistenceContext.class);
    }

    @Override
    public void preStartupAction(WeldSetupClass weldSetup, Class clazz, Method method) {
        for (Class<?> c : jpaTestExtensionServiceData.get().candidatesToStart) {
            if(!weldSetup.getBeanClasses().contains(c.getName())) {
                logger.warn("Entity, Mdb or Startup candidate: {} found "
                            + " while scanning availables, but not in testconfiguration included.", c.getSimpleName());
            }
        }
        jpaTestExtensionServiceData.get().candidatesToStart.clear(); // show only once
        if(weldSetup.isWeld3()) {
            if(!weldSetup.getBeanClasses().contains(SimulatedUserTransaction.class.getName())) {
                weldSetup.getBeanClasses().add(SimulatedUserTransaction.class.getName());
            }
        }
        if(!foundPersistenceFactory) {
            for (Metadata<Extension> x : weldSetup.getExtensions()) {
                if(EjbExtensionExtended.class.isAssignableFrom(x.getValue().getClass())) {
                    logger.warn("Using ioc-unit-ejb-Extension without IOC-Unit-PersistenceFactory: "
                                + "no simulation of EntityManager and Transactions supported");
                }
            }
        }

        weldSetup.addService(new WeldSetup.ServiceConfig(TransactionServices.class, new TransactionServicesImpl()));
    }
}
