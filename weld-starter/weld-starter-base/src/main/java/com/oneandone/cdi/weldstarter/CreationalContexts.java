package com.oneandone.cdi.weldstarter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to handle the initialisation of weld-beans
 *
 * @author aschoerk
 */
public class CreationalContexts<T> implements AutoCloseable {


    private final BeanManager bm;
    private List<CreationalContext<T>> creationalContexts = new ArrayList<>();
    private Logger logger = null;

    /**
     * Create and search for BeanManager in InitialContext
     *
     * @throws NamingException thrown when problems with InitialContext
     */
    public CreationalContexts() throws NamingException {
        BeanManager tmpBm = getBeanManager();
        this.bm = tmpBm;
    }

    /**
     * Create it
     *
     * @param bm the Weld-Beanmanager
     */
    public CreationalContexts(BeanManager bm) {
        this.bm = bm;
    }

    public static BeanManager getBeanManager() throws NamingException {
        BeanManager tmpBm = null;
        try {
            // the following code simulates: tmpBm = CDI.current().getBeanManager();
            Class<?> cdiClass = Class.forName("javax.enterprise.inject.spi.CDI");
            Method currentMethod = cdiClass.getMethod("current");
            Method getBeanManagerMethod = cdiClass.getMethod("getBeanManager");
            Object cdi = currentMethod.invoke(null);
            tmpBm = (BeanManager) getBeanManagerMethod.invoke(cdi);
        } catch (Exception e) {
            throw new RuntimeException("Cannot use javax.enterprise.inject.spi.CDI in this weld-version");
        }
        return tmpBm;
    }

    private Logger getLogger() {
        if(logger == null) {
            logger = LoggerFactory.getLogger("CreationalContexts");
        }
        return logger;
    }

    /**
     * create a bean of class clazz in context scope
     *
     * @param clazz the clazz of the Bean to be created
     * @param scope either ApplicationScoped or Dependent
     * @return the created bean
     */
    public Object create(Class<T> clazz, Class<? extends Annotation> scope, Annotation... qualifiers) {
        Bean<?> bean = bm.resolve(bm.getBeans(clazz, qualifiers));
        if(bean != null) {
            Object result = create((Contextual<T>) bean, scope);
            if(result == null) {
                throw new RuntimeException("Could not create Bean to be initialized of Class: " + clazz);
            }
            return result;
        }
        else {
            throw new RuntimeException("Could not resolve Bean to be initialized of Class: " + clazz);
        }
    }

    /**
     * create a bean in context
     *
     * @param b     the Bean as described and found by the weld init.
     * @param scope either ApplicationScoped or Dependent
     * @return the created bean
     */
    public Object create(Contextual<T> b, Class<? extends Annotation> scope) {
        try {
            final CreationalContext<T> cb = bm.createCreationalContext(b);
            // assumes the bean will exist only once
            Context context = bm.getContext(scope);
            final Object o = context.get(b, cb);
            creationalContexts.add(cb);
            return o;
        } catch (Throwable thw) {
            getLogger().error("Exception during create of Bean {}", b);
            getLogger().error("Exception: ", thw);
            throw new RuntimeException(thw);
        }
    }

    /**
     * close without checked exception
     */
    public void closeIt() {
        for (CreationalContext<?> cc : creationalContexts) {
            cc.release();
        }
    }

    /**
     * close according to AutoCloseable
     *
     * @throws Exception check Exception should not occur.
     */
    @Override
    public void close() throws Exception {
        closeIt();
    }
}
