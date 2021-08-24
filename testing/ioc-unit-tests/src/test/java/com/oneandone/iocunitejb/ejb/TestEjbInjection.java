package com.oneandone.iocunitejb.ejb;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.lang.reflect.Field;

import javax.ejb.EJB;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.AnalyzerFlags;
import com.oneandone.iocunit.analyzer.annotations.ExcludedClasses;
import com.oneandone.iocunit.analyzer.annotations.SutClasspaths;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.jpa.XmlLessPersistenceFactory;
import com.oneandone.iocunitejb.ClassWithTwoDifferentEntityManagers;
import com.oneandone.iocunitejb.cdibeans.AppScopedServiceBean;
import com.oneandone.iocunitejb.ejbs.ResourceTestEjb;
import com.oneandone.iocunitejb.ejbs.StatelessEJB;
import com.oneandone.iocunitejb.helpers.LoggerGenerator;
import com.oneandone.iocunitejb.resources.Resources;

/**
 * @author aschoerk
 */
@AnalyzerFlags(addAllStartableBeans = true)
@RunWith(IocUnitRunner.class)
@SutClasspaths({AppScopedServiceBean.class})
@TestClasses({LoggerGenerator.class, XmlLessPersistenceFactory.class})
@ExcludedClasses({Resources.class, ResourceTestEjb.class, ClassWithTwoDifferentEntityManagers.class})
public class TestEjbInjection {

    @EJB(name = "StatelessEJB")
    protected StatelessEJB statelessEJB;

    @Test
    public void testEjbName() {
        boolean fieldFound = false;
        for (Field f : this.getClass().getDeclaredFields()) {
            if (f.getName().equals("statelessEJB")) {
                fieldFound = true;
                EJB ann = f.getAnnotation(EJB.class);
                assertThat(ann, notNullValue());
                assertThat(ann.name(), is("StatelessEJB"));
            }
        }
        Assert.assertTrue(fieldFound);
    }


}
