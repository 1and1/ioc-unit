package com.oneandone.iocunit.jtajpa.helpers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.cdi.weldstarter.CreationalContexts;
import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.jtajpa.TestResources;

@RunWith(IocUnitRunner.class)
@TestClasses({TestResources.class})
public class QualTest {

    @Inject
    @PersQualifier(name = "ss")
    String s;

    @Inject
    BeanManager beanManager;

    @Test
    public void test() throws Exception {

        try (CreationalContexts creationalContexts = new CreationalContexts(beanManager)) {
            final PersQualifier.PersQualifierLiteral qualifier = new PersQualifier.PersQualifierLiteral("test", "", "");
            String res = (String) creationalContexts.create(String.class, ApplicationScoped.class, qualifier);
            Assert.assertTrue(s.equals("ss"));
            Assert.assertTrue(TestResources.sperqualifiers.contains("default"));
            Assert.assertTrue(TestResources.sperqualifiers.contains("ss"));
        }
    }

}