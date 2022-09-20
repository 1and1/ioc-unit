package com.oneandone.iocunit.jtajpa;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.cdi.weldstarter.CreationalContexts;
import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.jtajpa.helpers.PersQualifier;

@RunWith(IocUnitRunner.class)
public class QualTest {
    public static List<String> sperqualifiers = new ArrayList<>();
    @Inject
    @PersQualifier(name = "ss")
    String s;

    @Inject
    BeanManager beanManager;

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

    @Test
    public void test() throws Exception {

        try (CreationalContexts creationalContexts = new CreationalContexts(beanManager)) {
            final PersQualifier.PersQualifierLiteral qualifier = new PersQualifier.PersQualifierLiteral("test", "", "");
            String res = (String) creationalContexts.create(String.class, ApplicationScoped.class, qualifier);
            Assert.assertTrue(s.equals("ss"));
            Assert.assertTrue(sperqualifiers.contains("default"));
            Assert.assertTrue(sperqualifiers.contains("ss"));
        }
    }

}