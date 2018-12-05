package com.oneandone.cdi.testanalyzer;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import javax.inject.Inject;

import com.oneandone.cdi.weldstarter.spi.TestExtensionService;

/**
 * @author aschoerk
 */
public class AnalyzeConfiguration {
    Collection<TestExtensionService> testExtensionServices;
    List<Class<? extends Annotation>> injectAnnotations = new ArrayList<>();
    Map<Class<? extends Annotation>, TestExtensionService> extraClassAnnotations = new HashMap<>();
    List<Class<?>> initialClasses = new ArrayList<>();

    public AnalyzeConfiguration() {
        if (testExtensionServices == null) {
            testExtensionServices = new ArrayList<>();
            ServiceLoader<TestExtensionService> loader = ServiceLoader.load(TestExtensionService.class);
            final Iterator<TestExtensionService> testExtensionServiceIterator = loader.iterator();
            while (testExtensionServiceIterator.hasNext()) {
                testExtensionServices.add(testExtensionServiceIterator.next());
            }
            injectAnnotations.add(Inject.class);
            for (TestExtensionService testExtensionService : testExtensionServices) {
                injectAnnotations.addAll(testExtensionService.injectAnnotations());
                for (Class<? extends Annotation> annotation : testExtensionService.extraClassAnnotations()) {
                    extraClassAnnotations.put(annotation, testExtensionService);
                }
                initialClasses.addAll(testExtensionService.testClasses());
            }
        }
    }

}
