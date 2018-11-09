package com.oneandone.ejbcdiunit.weldstarter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.DeploymentException;

import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.api.CDI11Bootstrap;
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

public class WeldStarter {

    WeldContainer container;

    public void setUp(WeldSetup weldSetup) {

        try {
            start(weldSetup);
        } catch (DeploymentException ex) {
            weldSetup.setDeploymentException(ex);
        }
    }

    public void start(WeldSetup weldSetup) {
        System.setProperty("org.jboss.weld.bootstrap.concurrentDeployment", "false");

        Weld weld = new Weld() {

            protected Deployment createDeployment(final ResourceLoader resourceLoader, final CDI11Bootstrap bootstrap) {

                final ServiceRegistry services = new SimpleServiceRegistry();

                final BeanDeploymentArchive oneDeploymentArchive = createOneDeploymentArchive(weldSetup, services);

                oneDeploymentArchive.getServices().add(ResourceLoader.class, resourceLoader);

                Deployment res = new Post1Deployment(services, oneDeploymentArchive, weldSetup.getExtensions());

                return res;
            }

            protected Deployment createDeployment(final ResourceLoader resourceLoader, final Bootstrap bootstrap) {

                final ServiceRegistry services = new SimpleServiceRegistry();

                final BeanDeploymentArchive oneDeploymentArchive = createOneDeploymentArchive(weldSetup, services);

                oneDeploymentArchive.getServices().add(ResourceLoader.class, resourceLoader);

                Deployment res = new CDI1DeploymentImpl(services, oneDeploymentArchive, weldSetup.getExtensions());

                return res;
            }

        };
        container = weld.initialize();
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
                    Collections.EMPTY_LIST, // decorators
                    Collections.EMPTY_LIST, // interceptors
                    Scanning.EMPTY_SCANNING,
                    // These were added in Weld 2.0:
                    new URL("file:weld-starter"),
                    annotatedDiscoveryMode(),
                    "1.0",
                    // isTrimmed: added in Weld 2.4.2 [WELD-2314]:
                    false);
            return (BeansXml) Proxy.newProxyInstance(BeansXml.class.getClassLoader(), new Class<?>[] { BeansXml.class }, beansXmlImpl);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public void tearDown() {
        if (container != null)
            container.close();
        container = null;
    }
}
