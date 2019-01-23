package com.oneandone.iocunit.analyzer;

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
 * Searches for Tester Extensions and uses parts prepared to initialize the analyzation process.
 *
 * @author aschoerk
 */
public class TesterExtensionsConfigsFinder {
    Collection<TestExtensionService> testExtensionServices;
    List<Class<? extends Annotation>> injectAnnotations = new ArrayList<>();
    Map<Class<? extends Annotation>, TestExtensionService> extraClassAnnotations = new HashMap<>();
    List<Class<?>> initialClasses = new ArrayList<>();
    List<Class<?>> fakeClasses = new ArrayList<>();
    List<Class<?>> excludeFromInjectScan = new ArrayList<>();
    List<Class<?>> excludeAsInjects = new ArrayList<>();


    public TesterExtensionsConfigsFinder() {
        if (testExtensionServices == null) {
            testExtensionServices = new ArrayList<>();
            ServiceLoader<TestExtensionService> loader = ServiceLoader.load(TestExtensionService.class);
            final Iterator<TestExtensionService> testExtensionServiceIterator = loader.iterator();
            while (testExtensionServiceIterator.hasNext()) {
                testExtensionServices.add(testExtensionServiceIterator.next());
            }
            injectAnnotations.add(Inject.class);
            for (TestExtensionService testExtensionService : testExtensionServices) {
                testExtensionService.initAnalyze();
                injectAnnotations.addAll(testExtensionService.injectAnnotations());
                excludeFromInjectScan.addAll(testExtensionService.excludeFromIndexScan());
                for (Class<? extends Annotation> annotation : testExtensionService.extraClassAnnotations()) {
                    extraClassAnnotations.put(annotation, testExtensionService);
                }
                initialClasses.addAll(testExtensionService.testClasses());
                fakeClasses.addAll(testExtensionService.fakeClasses());
                excludeAsInjects.addAll(testExtensionService.excludeAsInjects());
            }
        }
    }

}
