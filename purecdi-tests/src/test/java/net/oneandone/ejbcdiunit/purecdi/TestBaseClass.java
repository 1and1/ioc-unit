package net.oneandone.ejbcdiunit.purecdi;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.enterprise.inject.spi.DeploymentException;
import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.bootstrap.api.CDI11Bootstrap;
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
import org.junit.After;
import org.junit.Before;

/**
 * @author aschoerk
 */
public abstract class TestBaseClass {
    WeldContainer container;

    protected DeploymentException deploymentException;

    protected Collection<String> getBeanClasses() {
        return Collections.EMPTY_LIST;
    }

    protected List<Metadata<String>> getAlternativeClasses() {
        return Collections.EMPTY_LIST;
    }

    protected List<Metadata<String>> getEnabledAlternativeStereotypes() {
        return Collections.EMPTY_LIST;
    }


    @Before
    public void setUp() {
        System.setProperty("org.jboss.weld.bootstrap.concurrentDeployment", "false");
        this.deploymentException = null;
        Weld weld = new Weld() {


            @Override
            protected Deployment createDeployment(final ResourceLoader resourceLoader, final CDI11Bootstrap bootstrap) {

                final ServiceRegistry services = new SimpleServiceRegistry();

                final BeanDeploymentArchive oneDeploymentArchive = createOneDeploymentArchive(services);

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
        try {
            container = weld.initialize();
        } catch (DeploymentException ex) {
            this.deploymentException = ex;
        }
    }

    private BeanDeploymentArchive createOneDeploymentArchive(final ServiceRegistry services) {
        return new BeanDeploymentArchive() {

            @Override
            public Collection<BeanDeploymentArchive> getBeanDeploymentArchives() {
                return Collections.EMPTY_LIST;
            }

            @Override
            public Collection<String> getBeanClasses() {
                return TestBaseClass.this.getBeanClasses();
            }

            @Override
            public BeansXml getBeansXml() {
                return createBeansXml();
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

    private BeansXml createBeansXml() {
        return new BeansXml() {
            // @Override only in case of weld3
            public boolean isTrimmed() {
                return false;
            }

            @Override
            public List<Metadata<String>> getEnabledAlternativeStereotypes() {
                return TestBaseClass.this.getEnabledAlternativeStereotypes();
            }

            @Override
            public List<Metadata<String>> getEnabledAlternativeClasses() {
                return TestBaseClass.this.getAlternativeClasses();
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


    @After
    public void tearDown() {
        if (container != null)
            container.close();
    }
}
