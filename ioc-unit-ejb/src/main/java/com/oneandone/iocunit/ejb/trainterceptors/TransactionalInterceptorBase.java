package com.oneandone.iocunit.ejb.trainterceptors;

import java.lang.annotation.Annotation;

import javax.ejb.MessageDriven;
import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.interceptor.InvocationContext;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.iocunit.ejb.EjbInformationBean;
import com.oneandone.iocunit.ejb.persistence.SimulatedTransactionManager;

/**
 * @author aschoerk
 */
public class TransactionalInterceptorBase {

    protected final Logger logger =
            LoggerFactory.getLogger(TransactionalInterceptorBase.class);
    protected SimulatedTransactionManager transactionManager = new SimulatedTransactionManager();

    static ThreadLocal<TransactionAttributeType> lastTransactionAttributeType = new ThreadLocal<>();
    static ThreadLocal<Integer> level = new ThreadLocal<>();

    static public int getLevel() {
        Integer actLevel = level.get();
        if (actLevel == null) {
            level.set(0);
            actLevel = 0;
        }
        return actLevel;
    }

    static public void incLevel() {
        level.set(getLevel() + 1);
    }

    static public void decLevel() {
        level.set(getLevel() - 1);
    }

    @Inject
    protected EjbInformationBean ejbInformationBean;



    protected Class<?> getTargetClass(InvocationContext ctx) {
        final Object target = ctx.getTarget();
        if (target == null)
            return null;
        Class<? extends Object> res = target.getClass();
        if (res.getName().endsWith("WeldSubclass"))
            return res.getSuperclass();
        else
            return res;

    }

    protected <T extends Annotation> T findAnnotation(Class<?> declaringClass, Class<T> annotationType) {
        if (declaringClass == null || declaringClass.equals(Object.class))
            return null;
        T annotation = declaringClass.getAnnotation(annotationType);
        if (annotation == null) {
            return findAnnotation(declaringClass.getSuperclass(), annotationType);
        } else {
            return annotation;
        }
    }

    protected boolean isBeanManaged(Class<?> declaringClass) {
        TransactionManagement annotation = findAnnotation(declaringClass, TransactionManagement.class);
        if (annotation == null)
            return false;
        else
            return annotation.value() == TransactionManagementType.BEAN;
    }

    protected boolean isNotTransactionalClass(Class<?> declaringClass) {
        if (declaringClass.equals(Object.class))
            return true;
        TransactionAttribute tmAnnotation = findAnnotation(declaringClass, TransactionAttribute.class);
        if (tmAnnotation != null)
            return false;
        else {
            boolean result = declaringClass != null
                             && declaringClass.getAnnotation(TransactionManagement.class) == null // allow transactionmanagement to
                             && declaringClass.getAnnotation(Transactional.class) == null // allow transactionmanagement to
                             // be defined for non ejb-classes
                             && declaringClass.getAnnotation(MessageDriven.class) == null
                             && declaringClass.getAnnotation(Stateless.class) == null
                             && declaringClass.getAnnotation(Stateful.class) == null
                             && declaringClass.getAnnotation(Singleton.class) == null;
            return result;
        }
    }


}
