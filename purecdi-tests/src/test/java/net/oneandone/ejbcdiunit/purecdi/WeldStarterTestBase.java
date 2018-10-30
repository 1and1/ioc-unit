package net.oneandone.ejbcdiunit.purecdi;

import java.util.ArrayList;
import java.util.Arrays;
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

        public void setBeanClasses(final Collection<String> beanClassesP) {
            this.beanClasses = beanClassesP;
        }

        public void setBeanClasses(String... classNames) {
            this.beanClasses = Arrays.asList(classNames);
        }

        public void setBeanClasses(Class... classes) {
            beanClasses = new ArrayList<>();
            for (Class clazz : classes) {
                beanClasses.add(clazz.getName());
            }
        }


        public void setAlternativeClasses(final List<Metadata<String>> alternativeClassesP) {
            this.alternativeClasses = alternativeClassesP;
        }

        public void setAlternativeClasses(String... classNames) {
            this.alternativeClasses = new ArrayList<>();
            for (String className : classNames) {
                alternativeClasses.add(new Metadata<String>() {
                    @Override
                    public String getValue() {
                        return className;
                    }

                    @Override
                    public String getLocation() {
                        return "In Testcode";
                    }
                });
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
                        return "In Testcode";
                    }
                });
            }
        }

        public void setEnabledAlternativeStereotypes(final List<Metadata<String>> enabledAlternativeStereotypesP) {
            this.enabledAlternativeStereotypes = enabledAlternativeStereotypesP;
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

    public void setAlternativeClasses(Class... classes) {
        weldSetup.setAlternativeClasses(classes);
    }

    public void start() {
        weldStarter.start(weldSetup);
    }

    WeldContainer getContainer() {
        return weldStarter.container;
    }

    Instance<Object> getContainerInstance() {
        return getContainer().instance();
    }

    <T> T selectGet(Class<T> clazz) {
        return getContainerInstance().select(clazz).get();
    }


    WeldSetupClass weldSetup = new WeldSetupClass();
    WeldStarter weldStarter = new WeldStarter();

    @After
    public void tearDown() {
        weldStarter.tearDown();
    }
}
