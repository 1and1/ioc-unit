package com.oneandone.cdi.weld1starter;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.bean.proxy.InterceptionDecorationContext;
import org.jboss.weld.bootstrap.WeldBootstrap;
import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.api.helpers.SimpleServiceRegistry;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.BeansXml;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.bootstrap.spi.Scanning;
import org.jboss.weld.ejb.spi.EjbDescriptor;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.environment.se.beans.InstanceManager;
import org.jboss.weld.exceptions.DeploymentException;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.jboss.weld.util.reflection.Formats;

import com.oneandone.cdi.weldstarter.BeansXmlImpl;
import com.oneandone.cdi.weldstarter.StarterDeploymentException;
import com.oneandone.cdi.weldstarter.WeldSetup;
import com.oneandone.cdi.weldstarter.spi.WeldStarter;

public class WeldStarterImpl implements WeldStarter {

    public WeldContainer container;
    private String version;

    Weld weld;

    public void start(WeldSetup weldSetup) {

        weldSetup.getBeanClasses().add(WeldContainer.class.getName());

        weldSetup.getBeanClasses().add(InstanceManager.class.getName());


        // weldSetup.getServices().add(new WeldSetup.ServiceConfig(org.jboss.weld.resources.spi.ScheduledExecutorServiceFactory.class,
        //         new SingleThreadScheduledExecutorServiceFactory()));

        this.version = Formats.version(WeldBootstrap.class.getPackage());
        System.setProperty("org.jboss.weld.bootstrap.concurrentDeployment", "false");

        weld = new Weld() {

            protected Deployment createDeployment(final ResourceLoader resourceLoader, final Bootstrap bootstrap) {

                final ServiceRegistry services = new SimpleServiceRegistry();
                weldSetup.registerServices(services);

                final BeanDeploymentArchive oneDeploymentArchive = createOneDeploymentArchive(weldSetup, services);

                oneDeploymentArchive.getServices().add(ResourceLoader.class, resourceLoader);

                Deployment res = new CDI1DeploymentImpl(services, oneDeploymentArchive, weldSetup.getExtensions());

                return res;
            }

        };
        try {
            container = weld.initialize();
        } catch (DeploymentException ex) {
            throw new StarterDeploymentException(ex);
        }
    }

    @Override
    public <T> T get(final Class<T> clazz, Annotation... qualifiers) {
        return container.instance().select(clazz, qualifiers).get();
    }

    @Override
    public String getVersion() {
        return Formats.version(WeldBootstrap.class.getPackage());
    }

    private BeanDeploymentArchive createOneDeploymentArchive(WeldSetup weldSetup, final ServiceRegistry services) {
        return new BeanDeploymentArchive() {
            private Set<String> beanClasses = null;

            @Override
            public Collection<BeanDeploymentArchive> getBeanDeploymentArchives() {
                return Collections.EMPTY_LIST;
            }

            @Override
            public Collection<String> getBeanClasses() {
                if (beanClasses == null) {
                    beanClasses = new HashSet<>();
                    beanClasses.addAll(weldSetup.getBeanClasses());
                }
                return beanClasses;
            }

            @Override
            public BeansXml getBeansXml() {
                return createBeansXml(weldSetup);
            }

            @Override
            public Collection<EjbDescriptor<?>> getEjbs() {
                return null;
            }

            @Override
            public ServiceRegistry getServices() {
                return services;
            }

            @Override
            public String getId() {
                return this.getClass().getSimpleName();
            }
        };
    }

    private BeansXml createBeansXml(WeldSetup weldSetup) {
        try {
            InvocationHandler beansXmlImpl = new BeansXmlImpl(
                    weldSetup.getAlternativeClasses(),
                    weldSetup.getEnabledAlternativeStereotypes(),
                    weldSetup.getEnabledDecorators(), // decorators
                    weldSetup.getEnabledInterceptors(), // interceptors
                    Scanning.EMPTY_SCANNING,
                    // These were added in Weld 2.0:
                    new URL("file:weld1-starter"),
                    null,
                    "1.0",
                    // isTrimmed: added in Weld 2.4.2 [WELD-2314]:
                    false);
            return (BeansXml) Proxy.newProxyInstance(BeansXml.class.getClassLoader(), new Class<?>[] { BeansXml.class }, beansXmlImpl);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public WeldContainer getContainer() {
        return container;
    }

    public Instance<Object> getContainerInstance() {
        return getContainer().instance();
    }


    public <T> T selectGet(Class<T> clazz) {
        return getContainerInstance().select(clazz).get();
    }


    public void tearDown() {
        try {
            if (weld != null)
                weld.shutdown();
        } catch (NullPointerException ex) {

        }
        container = null;
        weld = null;
    }

    private static boolean startInterceptionDecorationContextInner() {
        Method[] methods = InterceptionDecorationContext.class.getMethods();
        for (Method m : methods) {
            if (m.getParameterTypes().length == 0) {
                if (m.getName().equals("startInterceptorContext")) {
                    callMethodThrowRTEIfNecessary(m);
                    return true;
                }
                if (m.getName().equals("startIfNotEmpty") || m.getName().equals("startIfNotOnTop")) {
                    Object result = callMethodThrowRTEIfNecessary(m);
                    return result != null;
                }
            }
        }
        return false;
    }

    private static Object callMethodThrowRTEIfNecessary(Method m) {
        try {
            return m.invoke(null);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean startInterceptionDecorationContext() {
        return WeldStarterImpl.startInterceptionDecorationContextInner();
    }


    @Override
    public void endInterceptorContext() {
        InterceptionDecorationContext.endInterceptorContext();
    }

    @Override
    public Extension createExtension(String className) {
        try {
            return (Extension) (Class.forName(className).newInstance());
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getContainerId() {
        return "Weld1Container";
    }

}
