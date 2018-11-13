package com.oneandone.cdi.weldstarter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;

import org.jboss.weld.bootstrap.spi.Metadata;
import org.jboss.weld.bootstrap.spi.Scanning;

/**
 * @author aschoerk
 */
public class BeansXmlImpl implements InvocationHandler {

    private final List<Metadata<String>> enabledAlternatives;
    private final List<Metadata<String>> enabledAlternativeStereotypes;
    private final List<Metadata<String>> enabledDecorators;
    private final List<Metadata<String>> enabledInterceptors;
    private final Scanning scanning;
    private final URL url;
    private final Object discoveryMode;
    private final String version;
    private final boolean isTrimmed;

    public BeansXmlImpl(List<Metadata<String>> enabledAlternatives, List<Metadata<String>> enabledAlternativeStereotypes,
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

