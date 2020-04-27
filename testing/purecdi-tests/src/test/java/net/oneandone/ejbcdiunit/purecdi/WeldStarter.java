package net.oneandone.ejbcdiunit.purecdi;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.enterprise.inject.spi.DeploymentException;
import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.bootstrap.WeldBootstrap;
import org.jboss.weld.bootstrap.api.CDI11Bootstrap;
import org.jboss.weld.bootstrap.api.Service;
import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.api.helpers.SimpleServiceRegistry;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.BeanDiscoveryMode;
import org.jboss.weld.bootstrap.spi.BeansXml;
import org.jboss.weld.bootstrap.spi.CDI11Deployment;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.bootstrap.spi.Metadata;
import org.jboss.weld.bootstrap.spi.Scanning;
import org.jboss.weld.ejb.spi.EjbDescriptor;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.jboss.weld.util.reflection.Formats;

public class WeldStarter {

    WeldContainer container;

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
        System.setProperty("org.jboss.weld.bootstrap.concurrentDeployment", "false");

        Weld weld = new Weld() {

            @Override
            protected Deployment createDeployment(final ResourceLoader resourceLoader, final CDI11Bootstrap bootstrap) {
                String version = Formats.version(WeldBootstrap.class.getPackage());

                final ServiceRegistry services = new SimpleServiceRegistry();
                if (version.startsWith("2")) {
                    services.add(ScheduledExecutorServiceFactory.class, new ScheduledExecutorServiceFactory() {
                        @Override
                        public ScheduledExecutorService get() {
                            return new ScheduledThreadPoolExecutor(10);
                        }

                        @Override
                        public void cleanup() {

                        }
                    });
                }

                final BeanDeploymentArchive oneDeploymentArchive = createOneDeploymentArchive(weldSetup, services);

                oneDeploymentArchive.getServices().add(ResourceLoader.class, resourceLoader);

                Deployment res = new CDI11Deployment() {

                    @Override
                    public BeanDeploymentArchive getBeanDeploymentArchive(final Class<?> beanClass) {
                        return oneDeploymentArchive;
                    }

                    @Override
                    public Collection<BeanDeploymentArchive> getBeanDeploymentArchives() {
                        return Arrays.asList(oneDeploymentArchive);
                    }

                    @Override
                    public BeanDeploymentArchive loadBeanDeploymentArchive(final Class<?> beanClass) {
                        return oneDeploymentArchive;
                    }

                    @Override
                    public ServiceRegistry getServices() {
                        return oneDeploymentArchive.getServices();
                    }

                    @Override
                    public Iterable<Metadata<Extension>> getExtensions() {
                        return Collections.emptyList();
                    }
                };

                return res;
            }


        };
        container = weld.initialize();
    }

    private BeanDeploymentArchive createOneDeploymentArchive(WeldSetup weldSetup, final ServiceRegistry services) {
        return new BeanDeploymentArchive() {

            @Override
            public Collection<BeanDeploymentArchive> getBeanDeploymentArchives() {
                return Collections.EMPTY_LIST;
            }

            @Override
            public Collection<String> getBeanClasses() {
                return weldSetup.getBeanClasses();
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
        return new BeansXml() {
            // @Override only in case of weld3
            public boolean isTrimmed() {
                return false;
            }

            @Override
            public List<Metadata<String>> getEnabledAlternativeStereotypes() {
                return weldSetup.getEnabledAlternativeStereotypes();
            }

            @Override
            public List<Metadata<String>> getEnabledAlternativeClasses() {
                return weldSetup.getAlternativeClasses();
            }

            @Override
            public List<Metadata<String>> getEnabledDecorators() {
                return Collections.EMPTY_LIST;
            }

            @Override
            public List<Metadata<String>> getEnabledInterceptors() {
                return Collections.EMPTY_LIST;
            }

            @Override
            public Scanning getScanning() {
                return Scanning.EMPTY_SCANNING;
            }

            @Override
            public URL getUrl() {
                return null;
            }

            @Override
            public BeanDiscoveryMode getBeanDiscoveryMode() {
                return BeanDiscoveryMode.ALL;
            }

            @Override
            public String getVersion() {
                return "1.0";
            }
        };
    }

    public void tearDown() {
        if(container != null) {
            container.close();
        }
        container = null;
    }
}
