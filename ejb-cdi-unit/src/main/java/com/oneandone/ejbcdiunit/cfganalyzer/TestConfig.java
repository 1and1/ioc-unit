package com.oneandone.ejbcdiunit.cfganalyzer;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.bootstrap.spi.Metadata;

public class TestConfig {
    private Set<URL> cdiClasspathEntries = new HashSet<URL>();
    private Set<String> discoveredClasses = new LinkedHashSet<String>();
    private Collection<Metadata<String>> alternatives = new ArrayList<Metadata<String>>();
    private Class<?> ejbJarClasspathExample = null;
    private Collection<Metadata<? extends Extension>> extensions = new ArrayList<Metadata<? extends Extension>>();
    private Collection<Metadata<String>> enabledInterceptors = new ArrayList<Metadata<String>>();
    private Collection<Metadata<String>> enabledDecorators = new ArrayList<Metadata<String>>();
    private Collection<Metadata<String>> enabledAlternativeStereotypes = new ArrayList<Metadata<String>>();
    private Set<URL> classpathEntries = new HashSet<>();

    public TestConfig() {}

    public Set<URL> getCdiClasspathEntries() {
        return cdiClasspathEntries;
    }

    public Set<String> getDiscoveredClasses() {
        return discoveredClasses;
    }

    public Collection<Metadata<String>> getAlternatives() {
        return alternatives;
    }

    public Class<?> getEjbJarClasspathExample() {
        return ejbJarClasspathExample;
    }

    public void setEjbJarClasspathExample(final Class<?> ejbJarClasspathExample) {
        this.ejbJarClasspathExample = ejbJarClasspathExample;
    }

    public Collection<Metadata<? extends Extension>> getExtensions() {
        return extensions;
    }

    public Collection<Metadata<String>> getEnabledInterceptors() {
        return enabledInterceptors;
    }

    public Collection<Metadata<String>> getEnabledDecorators() {
        return enabledDecorators;
    }

    public Collection<Metadata<String>> getEnabledAlternativeStereotypes() {
        return enabledAlternativeStereotypes;
    }

    public Set<URL> getClasspathEntries() {
        return classpathEntries;
    }
}