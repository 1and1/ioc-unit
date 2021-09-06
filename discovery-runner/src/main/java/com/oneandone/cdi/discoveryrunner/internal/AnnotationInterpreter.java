package com.oneandone.cdi.discoveryrunner.internal;

import static java.util.Arrays.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.platform.commons.support.AnnotationSupport;

import com.oneandone.cdi.discoveryrunner.annotations.EnabledAlternatives;
import com.oneandone.cdi.discoveryrunner.annotations.ExcludedClasses;
import com.oneandone.cdi.discoveryrunner.annotations.TestClasses;

/**
 * @author aschoerk
 */
public class AnnotationInterpreter {
    public static void prepareWeldInfo(Class<?> clazzP, WeldInfo weldInfoP) {
        Collection<Class<?>> toScan = Arrays.asList(clazzP);
        List<Class<?>> didScan = new ArrayList<>();
        // weldInfoP.toAdd.add(clazzP);
        while (toScan.size() > 0) {
            Set<Class<?>> nextToScan = new HashSet<>();
            toScan.forEach(c -> {
                AnnotationSupport.findRepeatableAnnotations(c, ExcludedClasses.class)
                        .stream()
                        .flatMap(ann -> stream(ann.value()))
                        .map(Class::getName)
                        .forEach(weldInfoP.getToExcludeClassNames()::add);
                AnnotationSupport.findRepeatableAnnotations(c, ExcludedClasses.class)
                        .stream()
                        .flatMap(ann -> stream(ann.expressions()))
                        .forEach(weldInfoP.getToExcludeExpressions()::add);
                AnnotationSupport.findRepeatableAnnotations(c, ExcludedClasses.class)
                        .stream()
                        .flatMap(ann -> stream(ann.nameParts()))
                        .forEach(weldInfoP.getToExcludeClassNameParts()::add);
                AnnotationSupport.findRepeatableAnnotations(c, EnabledAlternatives.class)
                        .stream()
                        .flatMap(ann -> stream(ann.value()))
                        .peek(nextToScan::add)
                        .forEach(weldInfoP.getAlternatives()::add);
                AnnotationSupport.findRepeatableAnnotations(c, TestClasses.class)
                        .stream()
                        .flatMap(ann -> stream(ann.value()))
                        .peek(nextToScan::add)
                        .forEach(weldInfoP.getToAdd()::add);
            });
            didScan.addAll(toScan);
            nextToScan.removeAll(didScan);
            toScan = nextToScan;
        }
    }
}
