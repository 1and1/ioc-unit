package com.oneandone.iocunitejb.cdibeans;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.AnalyzerFlags;
import com.oneandone.iocunit.analyzer.annotations.ExcludedClasses;
import com.oneandone.iocunit.analyzer.annotations.SutClasspaths;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.jpa.XmlLessPersistenceFactory;
import com.oneandone.iocunitejb.ClassWithTwoDifferentEntityManagers;
import com.oneandone.iocunitejb.ejbs.ResourceTestEjb;
import com.oneandone.iocunitejb.resources.Resources;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@AnalyzerFlags(addAllStartableBeans = true)
@SutClasspaths({AppScopedServiceBean.class})
@TestClasses({XmlLessPersistenceFactory.class})
@ExcludedClasses({ResourceTestEjb.class, ClassWithTwoDifferentEntityManagers.class, Resources.class})
public class CdiBeansTest {

    @Inject
    AppScopedServiceBean appScopedServiceBean;


    @Test
    public void test() {
        appScopedServiceBean.getServiceBeanHelper().sumInitValues();
    }
}
