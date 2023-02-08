package com.oneandone.iocunit.ejb.resourcesimulators;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import javax.xml.ws.handler.MessageContext;

import jakarta.ejb.EJBLocalObject;
import jakarta.ejb.EJBObject;
import jakarta.ejb.MessageDrivenContext;
import jakarta.ejb.SessionContext;
import jakarta.enterprise.inject.Vetoed;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;

import com.oneandone.cdi.weldstarter.WeldSetupClass;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;

/**
 * cdi-unit simulation of sessioncontext
 *
 * @author aschoerk
 */
@Vetoed
public class SessionContextSimulation extends EjbContextSimulation implements SessionContext, MessageDrivenContext {

    private BeanManager beanManager;

    /**
     * this Object may only be created using new.
     *
     * @param preventInject has no function, just prevent default constructor
     */
    public SessionContextSimulation(String preventInject) {
        assert beanManager == null;
    }

    /**
     * Used later to be able to handle getBusinessObject
     *
     * @param containerP the injecting bean (originally having @Resource)
     * @param beanManagerP the CDI-Beanmanager
     */
    public void setContainer(Bean<?> containerP, BeanManager beanManagerP) {
        this.beanManager = beanManagerP;
    }



    /**
     * Obtain a reference to the EJB local object that is
     * associated with the instance.
     * <p> An instance of a session bean can call this method at
     * anytime between the <code>PostConstruct</code> or
     * <code>ejbCreate</code> and <code>PreDestroy</code> or
     * <code>ejbRemove</code> methods, including from within these
     * methods.
     * <p> An instance can use this method, for example, when it wants to
     * pass a reference to itself in a method argument or result.
     *
     * @return The EJB local object currently associated with the instance.
     * @throws IllegalStateException Thrown if the instance invokes this
     *                               method while the instance is in a state that does not allow the
     *                               instance to invoke this method, or if the instance does not have
     *                               a local interface.
     * @since EJB 2.0
     */
    @Override
    public EJBLocalObject getEJBLocalObject() throws IllegalStateException {
        throw new NotImplementedException("getEJBLocalObject not implemented in SessionContextSimulation of ejb-cdi-unit");
    }

    /**
     * Obtain a reference to the EJB object that is currently associated with
     * the instance.
     * <p> An instance of a session enterprise Bean can call this
     * method at anytime between the <code>PostConstruct</code> or
     * <code>ejbCreate</code> and the <code>PreDestroy</code> or
     * <code>ejbRemove</code> methods, including from within these
     * methods.
     * </p>
     * <p> An instance can use this method, for example, when it wants to
     * pass a reference to itself in a method argument or result.
     *
     * @return The EJB object currently associated with the instance.
     * @throws IllegalStateException Thrown if the instance invokes this
     *                               method while the instance is in a state that does not allow the
     *                               instance to invoke this method, or if the instance does not have
     *                               a remote interface.
     */
    @Override
    public EJBObject getEJBObject() throws IllegalStateException {
        throw new NotImplementedException("getObject not implemented in SessionContextSimulation of ejb-cdi-unit");
    }

     /**
     * Obtain an object that can be used to invoke the current bean through
     * a particular business interface view or its no-interface view.
     *
     * @param businessInterface One of the local business interfaces
     *                          or remote business interfaces for this session bean.
     *                          In addition, the bean class type can be used to acquire
     *                          a reference to the bean's no-interface view.
     * @return The business object corresponding to the given business
     * interface or no-interface view.
     * @throws IllegalStateException Thrown if invoked with a parameter
     *                               that does not correspond to one of the beans' business interfaces
     *                               or no-interface view.
     * @since EJB 3.0
     */
    @Override
    public <T> T getBusinessObject(Class<T> businessInterface) throws IllegalStateException {
        Set<Bean<?>> beans = beanManager.getBeans(businessInterface);
        if (beans.isEmpty() && businessInterface.getName().endsWith("_WeldSubclass")) {
            beans = beanManager.getBeans(businessInterface.getSuperclass());
        }
        Bean<T> bean = (Bean<T>) beanManager.resolve(beans);

        final Object testBean1 = beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean));

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(businessInterface);
        enhancer.setCallback(new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                WeldSetupClass.getWeldStarter().startInterceptionDecorationContext();
                try {
                    method.setAccessible(true);
                    return method.invoke(testBean1, objects);
                } catch (Throwable thw) {
                    if (thw instanceof InvocationTargetException) {
                        throw thw.getCause();
                    } else {
                        throw thw;
                    }
                } finally {
                    WeldSetupClass.getWeldStarter().endInterceptorContext();
                }
            }
        });

        Object proxy = enhancer.create();

        return (T) proxy;
    }

    /**
     * Obtain the business interface or no-interface view type through which the
     * current business method invocation was made.
     *
     * @throws IllegalStateException Thrown if this method is called
     *                               and the bean has not been invoked through a business interface or
     *                               no-interface view.
     * @since EJB 3.0
     */
    @Override
    public Class getInvokedBusinessInterface() throws IllegalStateException {
        throw new NotImplementedException("getInvokedBusinessInterface not implemented in SessionContextSimulation of ejb-cdi-unit");
    }

    /**
     * Check whether a client invoked the <code>cancel</code> method on the
     * client <code>Future</code> object corresponding to the currently executing
     * asynchronous business method.
     *
     * @return true if the client has invoked <code>Future.cancel</code> with a value of
     * true for the <code>mayInterruptIfRunning</code> parameter.
     * @throws IllegalStateException Thrown if not invoked from within an
     *                               asynchronous business method invocation with return type
     *                               <code>Future&#060;V&#062;</code>.
     * @since EJB 3.1
     */
    @Override
    public boolean wasCancelCalled() throws IllegalStateException {
        return false;
    }



}
