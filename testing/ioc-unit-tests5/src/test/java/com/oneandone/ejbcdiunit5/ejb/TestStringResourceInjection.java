package com.oneandone.ejbcdiunit5.ejb;

import javax.ejb.EJB;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.iocunit.IocJUnit5Extension;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.resource.ResourceQualifier;
import com.oneandone.iocunitejb.ejbs.ResourceTestEjb;

/**
 * @author aschoerk
 */
@ExtendWith(IocJUnit5Extension.class)
@TestClasses({ResourceTestEjb.class, TestStringResourceInjection.TestResources.class})
public class TestStringResourceInjection {
    @EJB
    ResourceTestEjb resourceTestEjb;

    @Singleton
    static class TestResources {

        @Produces
        @ResourceQualifier(name = "ResourceTestEjbAppName", lookup = "java:app/AppName", mappedName = "RTEAppName")
        String appNameProducer() {
            return "TestStringResourceInjectionAppName";
        }

        @Produces
        @ResourceQualifier(name = "ResourceTestEjbModuleName", lookup = "java:module/ModuleName", mappedName = "RTEModuleName")
        String moduleNameProducer() {
            return "TestStringResourceInjectionModuleName";
        }

        @Produces
        @ResourceQualifier(lookup = "java:app/AppName")
        String appName2Producer() {
            return "TestStringResourceInjectionAppName2";
        }

        @Produces
        @ResourceQualifier(lookup = "java:module/ModuleName")
        String moduleName2Producer() {
            return "TestStringResourceInjectionModuleName2";
        }

    }



    @Test
    public void canInjectFullSpecified() {

        Assertions.assertEquals("TestStringResourceInjectionAppName", resourceTestEjb.ejbAppName());
        Assertions.assertEquals("TestStringResourceInjectionModuleName", resourceTestEjb.ejbModuleName());

    }

    @Test
    public void canInjectPartlySpecified() {

        Assertions.assertEquals("TestStringResourceInjectionAppName2", resourceTestEjb.ejbAppName2());
        Assertions.assertEquals("TestStringResourceInjectionModuleName2", resourceTestEjb.ejbModuleName2());

    }

}
