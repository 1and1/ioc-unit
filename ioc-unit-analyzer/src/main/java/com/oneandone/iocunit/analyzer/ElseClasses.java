package com.oneandone.iocunit.analyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.decorator.Decorator;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.spi.Extension;
import javax.interceptor.Interceptor;

/**
 * @author aschoerk
 */
public class ElseClasses {
    Set<Class<?>> foundAlternativeStereotypes = new HashSet<>();
    Set<Class<?>> foundAlternativeClasses = new HashSet<>();
    List<Class<?>> decorators = new ArrayList<>();
    List<Class<?>> interceptors = new ArrayList<>();

    Set<Class<? extends Extension>> extensionClasses = new HashSet<>();
    List<Extension> extensionObjects = new ArrayList<>();
    Set<Class<?>> elseClasses = new HashSet<>();

    void elseClass(Class<?> c) {
        if(ConfigStatics.isExtension(c)) {
            extensionClasses.add((Class<? extends Extension>) c);
        }
        else if(c.getAnnotation(Decorator.class) != null) {
            decorators.add(c);
        }
        else if(c.getAnnotation(Interceptor.class) != null) {
            interceptors.add(c);
        }
        else if(c.isAnnotation()) {
            if(c.isAnnotationPresent(Stereotype.class) && c.isAnnotationPresent(Alternative.class)) {
                foundAlternativeStereotypes.add(c);
            }
            else {
                elseClasses.add(c);
            }
        }
        else {
            elseClasses.add(c);
        }
    }

}
