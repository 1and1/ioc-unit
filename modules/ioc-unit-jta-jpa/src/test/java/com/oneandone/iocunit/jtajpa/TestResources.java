package com.oneandone.iocunit.jtajpa;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import com.oneandone.iocunit.jtajpa.helpers.PersQualifier;

/**
 * @author aschoerk
 */
public class TestResources {
    public static List<String> sperqualifiers = new ArrayList<>();

    @Produces
    @PersQualifier
    String producer(InjectionPoint p) {
        if(p != null && p.getQualifiers() != null) {
            for (Annotation a : p.getQualifiers()) {
                System.out.println(a.toString());
                if(a.annotationType().equals(PersQualifier.class)) {
                    final String name = ((PersQualifier) a).name();
                    sperqualifiers.add(name);
                    return name;
                }
            }
        }
        sperqualifiers.add("default");
        return "default";
    }

}
