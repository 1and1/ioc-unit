package com.oneandone.ejbcdiunit.cdiunit;

/*
 *    Copyright 2011 Bryn Cooke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * copied from cdi-unit 3.1.4
 */

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.BeanDiscoveryMode;
import org.jboss.weld.bootstrap.spi.BeansXml;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.bootstrap.spi.Metadata;
import org.jboss.weld.bootstrap.spi.Scanning;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.cdiunit.BeanDeploymentArchiveImpl;
import com.oneandone.ejbcdiunit.CdiTestConfig;
import com.oneandone.ejbcdiunit.cfganalyzer.CdiUnitAnalyzer;
import com.oneandone.ejbcdiunit.cfganalyzer.TestConfig;

public class WeldTestUrlDeployment implements Deployment {
    private static Logger log = LoggerFactory.getLogger(WeldTestUrlDeployment.class);
    private final BeanDeploymentArchive beanDeploymentArchive;
    private Collection<Metadata<Extension>> extensions = new ArrayList<Metadata<Extension>>();
    private Set<URL> cdiClasspathEntries = new HashSet<URL>();

    public WeldTestUrlDeployment(ResourceLoader resourceLoader, Bootstrap bootstrap, WeldTestConfig weldTestConfig) throws IOException {

        Class<?> testClass = weldTestConfig.getTestClass();
        Method testMethod = weldTestConfig.getTestMethod();
        CdiUnitAnalyzer cdiUnitAnalyzer = new CdiUnitAnalyzer();
        cdiUnitAnalyzer.analyze(testClass, testMethod, weldTestConfig);

        TestConfig testConfig = cdiUnitAnalyzer.getTestConfig();
        BeansXml beansXml = createBeansXml();

        for (Metadata<String> eI : testConfig.getEnabledInterceptors()) {
            beansXml.getEnabledInterceptors().add(eI);
        }

        for (Metadata<String> eD : testConfig.getEnabledDecorators()) {
            beansXml.getEnabledDecorators().add(eD);
        }

        for (Metadata<String> eAS : testConfig.getEnabledAlternativeStereotypes()) {
            beansXml.getEnabledAlternativeStereotypes().add(eAS);
        }

        for (Metadata<String> alternative : testConfig.getAlternatives()) {

            beansXml.getEnabledAlternativeClasses().add(alternative);
        }


        beanDeploymentArchive = new BeanDeploymentArchiveImpl("cdi-unit" + UUID.randomUUID(), testConfig.getDiscoveredClasses(), beansXml);
        beanDeploymentArchive.getServices().add(ResourceLoader.class, resourceLoader);
        for (CdiTestConfig.ServiceConfig serviceConfig : weldTestConfig.getServiceConfigs()) {
            beanDeploymentArchive.getServices().add(serviceConfig.getServiceClass(), serviceConfig.getService());
        }

        for (Metadata<? extends Extension> e : testConfig.getExtensions()) {
            extensions.add((Metadata<Extension>) e);
        }

        log.trace("CDI-Unit discovered:");
        for (String clazz : testConfig.getDiscoveredClasses()) {
            if (!clazz.startsWith("org.jglue.cdiunit.internal.")) {
                log.trace(clazz);
            }
        }
    }

    private static Object annotatedDiscoveryMode() {
        try {
            return BeanDiscoveryMode.ANNOTATED;
        } catch (NoClassDefFoundError e) {
            // No such enum in Weld 1.x, but the constructor for BeansXmlImpl has fewer parameters so we don't need it
            return null;
        }
    }

    private static BeansXml createBeansXml() {
        try {
            InvocationHandler beansXmlImpl = new LocalBeansXmlImpl(new ArrayList<Metadata<String>>(), new ArrayList<Metadata<String>>(),
                    new ArrayList<Metadata<String>>(), new ArrayList<Metadata<String>>(), Scanning.EMPTY_SCANNING,
                    // These were added in Weld 2.0:
                    new URL("file:cdi-unit"), annotatedDiscoveryMode(), "cdi-unit",
                    // isTrimmed: added in Weld 2.4.2 [WELD-2314]:
                    false);
            return (BeansXml) Proxy.newProxyInstance(BeansXml.class.getClassLoader(), new Class<?>[] { BeansXml.class }, beansXmlImpl);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    static class LocalBeansXmlImpl implements InvocationHandler {

        private final List enabledAlternatives;
        private final List enabledAlternativeStereotypes;
        private final List enabledDecorators;
        private final List enabledInterceptors;
        private final Scanning scanning;
        private final URL url;
        private final Object discoveryMode;
        private final String version;
        private final boolean isTrimmed;

        public LocalBeansXmlImpl(List<Metadata<String>> enabledAlternatives, List<Metadata<String>> enabledAlternativeStereotypes,
                List<Metadata<String>> enabledDecorators, List<Metadata<String>> enabledInterceptors, Scanning scanning, URL url,
                Object discoveryMode,
                String version, boolean isTrimmed) {
            this.enabledAlternatives = enabledAlternatives;
            this.enabledAlternativeStereotypes = enabledAlternativeStereotypes;
            this.enabledDecorators = enabledDecorators;
            this.enabledInterceptors = enabledInterceptors;
            this.scanning = scanning;
            this.url = url;
            this.discoveryMode = discoveryMode;
            this.version = version;
            this.isTrimmed = isTrimmed;
        }


        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            switch (methodName) {
                case "getEnabledAlternativeClasses":
                    return enabledAlternatives;
                case "getEnabledAlternativeStereotypes":
                    return enabledAlternativeStereotypes;
                case "getEnabledDecorators":
                    return enabledDecorators;
                case "getEnabledInterceptors":
                    return enabledInterceptors;
                case "getScanning":
                    return scanning;
                case "getUrl":
                    return url;
                case "getBeanDiscoveryMode":
                    return discoveryMode;
                case "getVersion":
                    return version;
                case "isTrimmed":
                    return isTrimmed;
                default:
                    throw new RuntimeException("unknown method " + methodName + " of BeansXmlImpl");
            }
        }
    }

    @Override
    public Iterable<Metadata<Extension>> getExtensions() {
        return extensions;
    }

    public List<BeanDeploymentArchive> getBeanDeploymentArchives() {
        return Collections.singletonList(beanDeploymentArchive);
    }

    public BeanDeploymentArchive loadBeanDeploymentArchive(Class<?> beanClass) {
        return beanDeploymentArchive;
    }

    public BeanDeploymentArchive getBeanDeploymentArchive(Class<?> beanClass) {
        return beanDeploymentArchive;
    }

    @Override
    public ServiceRegistry getServices() {
        return beanDeploymentArchive.getServices();
    }
}
