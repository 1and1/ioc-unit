package com.oneandone.ejbcdiunit.weldstarter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.DeploymentException;

import org.jboss.weld.bootstrap.spi.Metadata;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.After;

/**
 * @author aschoerk
 */
public class WeldStarterTestBase {
    static class WeldSetupClass implements WeldSetup {
        private Collection<String> beanClasses = Collections.EMPTY_LIST;
        private List<Metadata<String>> alternativeClasses = Collections.EMPTY_LIST;
        private List<Metadata<String>> enabledAlternativeStereotypes = Collections.EMPTY_LIST;

        public void setBeanClasses(Class... classes) {
            beanClasses = new ArrayList<>();
            for (Class clazz : classes) {
                beanClasses.add(clazz.getName());
            }
        }


        public void setAlternativeClasses(Class... classes) {
            this.alternativeClasses = new ArrayList<>();
            for (Class clazz : classes) {
                alternativeClasses.add(new Metadata<String>() {
                    @Override
                    public String getValue() {
                        return clazz.getName();
                    }

                    @Override
                    public String getLocation() {
                        return "Alternative In Testcode";
                    }
                });
            }
        }


        public void setEnabledAlternativeStereotypes(Class... classes) {
            this.enabledAlternativeStereotypes = new ArrayList<>();
            for (Class clazz : classes) {
                enabledAlternativeStereotypes.add(new Metadata<String>() {
                    @Override
                    public String getValue() {
                        return clazz.getName();
                    }

                    @Override
                    public String getLocation() {
                        return "AlternativeStereotype In Testcode";
                    }
                });
            }
        }


        @Override
        public Collection<String> getBeanClasses() {
            return beanClasses;
        }

        @Override
        public List<Metadata<String>> getAlternativeClasses() {
            return alternativeClasses;
        }

        @Override
        public List<Metadata<String>> getEnabledAlternativeStereotypes() {
            return enabledAlternativeStereotypes;
        }

        @Override
        public void setDeploymentException(DeploymentException deploymentException) {

        }
    };

    public void setBeanClasses(Class... classes) {
        weldSetup.setBeanClasses(classes);
    }

    public void setBeanClasses(Collection<Class<?>> classes) {
        weldSetup.setBeanClasses(classes.toArray(new Class<?>[classes.size()]));
    }

    public void setAlternativeClasses(Class... classes) {
        weldSetup.setAlternativeClasses(classes);
    }

    public void setEnabledAlternativeStereotypes(Class... classes) {
        weldSetup.setEnabledAlternativeStereotypes(classes);
    }

    public void start() {
        weldStarter.start(weldSetup);
    }

    WeldContainer getContainer() {
        return weldStarter.container;
    }

    public Instance<Object> getContainerInstance() {
        return getContainer().instance();
    }

    public <T> T selectGet(Class<T> clazz) {
        return getContainerInstance().select(clazz).get();
    }


    WeldSetupClass weldSetup = new WeldSetupClass();
    WeldStarter weldStarter = new WeldStarter();

    @After
    public void tearDown() {
        weldStarter.tearDown();
    }
}
