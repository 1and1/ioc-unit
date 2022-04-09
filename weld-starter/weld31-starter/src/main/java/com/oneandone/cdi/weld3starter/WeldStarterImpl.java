package com.oneandone.cdi.weld3starter;

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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.DeploymentException;
import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.bean.proxy.InterceptionDecorationContext;
import org.jboss.weld.bootstrap.WeldBootstrap;
import org.jboss.weld.bootstrap.api.CDI11Bootstrap;
import org.jboss.weld.bootstrap.api.Service;
import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.api.helpers.SimpleServiceRegistry;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.BeanDiscoveryMode;
import org.jboss.weld.bootstrap.spi.BeansXml;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.bootstrap.spi.Scanning;
import org.jboss.weld.ejb.spi.EjbDescriptor;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.jboss.weld.util.reflection.Formats;

import com.oneandone.cdi.weldstarter.BeansXmlImpl;
import com.oneandone.cdi.weldstarter.StarterDeploymentException;
import com.oneandone.cdi.weldstarter.WeldSetup;
import com.oneandone.cdi.weldstarter.spi.WeldStarter;


public class WeldStarterImpl implements WeldStarter {

    public WeldContainer container;
    private String version;

    public void setUp(WeldSetup weldSetup) {

        try {
            start(weldSetup);
        } catch (DeploymentException ex) {
            weldSetup.setDeploymentException(ex);
        }
    }

    public interface ScheduledExecutorServiceFactory extends Service {
        ScheduledExecutorService get();
    }

    public void start(WeldSetup weldSetup) {
        WeldSetup.ServiceConfig serviceConfig = new WeldSetup.ServiceConfig(ScheduledExecutorServiceFactory.class,
                new ScheduledExecutorServiceFactory() {
                    @Override
                    public ScheduledExecutorService get() {
                        return new ScheduledThreadPoolExecutor(10);
                    }

                    @Override
                    public void cleanup() {

                    }
                });
        weldSetup.getServices().add(serviceConfig);
        this.version = Formats.version(WeldBootstrap.class.getPackage());
        System.setProperty("org.jboss.weld.bootstrap.concurrentDeployment", "false");

        Weld weld = new Weld("WeldStarter" + weldSetup.getNewInstanceNumber()) {

            protected Deployment createDeployment(final ResourceLoader resourceLoader, final CDI11Bootstrap bootstrap) {

                final ServiceRegistry services = new SimpleServiceRegistry();
                weldSetup.registerServices(services);

                final BeanDeploymentArchive oneDeploymentArchive = createOneDeploymentArchive(weldSetup, services);

                oneDeploymentArchive.getServices().add(ResourceLoader.class, resourceLoader);

                Deployment res = new Post1Deployment(services, oneDeploymentArchive, weldSetup.getExtensions());

                return res;
            }

        };
        try {
            weld.disableDiscovery();
            container = weld.initialize();
        } catch (DeploymentException ex) {
            throw new StarterDeploymentException(ex);
        }
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

    private static Object annotatedDiscoveryMode() {
        try {
            return BeanDiscoveryMode.ANNOTATED;
        } catch (NoClassDefFoundError e) {
            // No such enum in Weld 1.x, but the constructor for BeansXmlImpl has fewer parameters so we don't need it
            return null;
        }
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
                    new URL("file:weld31-starter"),
                    annotatedDiscoveryMode(),
                    "1.0",
                    // isTrimmed: added in Weld 2.4.2 [WELD-2314]:
                    false);
            return (BeansXml) Proxy.newProxyInstance(BeansXml.class.getClassLoader(), new Class<?>[] { BeansXml.class }, beansXmlImpl);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T get(final Class<T> clazz, Annotation... qualifiers) {
        return container.instance().select(clazz, qualifiers).get();
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
        if (container != null)
            container.close();
        container = null;
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

    public static Object callMethodThrowRTEIfNecessary(Method m) {
        try {
            return m.invoke(null);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String getVersion() {
        return Formats.version(WeldBootstrap.class.getPackage());
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
        return container.getId();
    }

}
