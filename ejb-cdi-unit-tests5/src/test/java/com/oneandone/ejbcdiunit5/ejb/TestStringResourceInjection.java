package com.oneandone.ejbcdiunit5.ejb;

import com.oneandone.ejbcdiunit.ResourceQualifier;
import com.oneandone.ejbcdiunit.ejbs.ResourceTestEjb;
import com.oneandone.ejbcdiunit5.JUnit5Extension;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ejb.EJB;
import javax.enterprise.inject.Produces;

/**
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
@AdditionalClasses(ResourceTestEjb.class)
public class TestStringResourceInjection {
    @EJB
    ResourceTestEjb resourceTestEjb;

    @Produces
    @ResourceQualifier(name = "ResourceTestEjbAppName", lookup = "java:app/AppName", mappedName = "RTEAppName")
    String appNameProducer() { return "TestStringResourceInjectionAppName"; }

    @Produces
    @ResourceQualifier(name = "ResourceTestEjbModuleName", lookup = "java:module/ModuleName", mappedName = "RTEModuleName")
    String moduleNameProducer() { return "TestStringResourceInjectionModuleName"; }

    @Produces
    @ResourceQualifier(lookup = "java:app/AppName")
    String appName2Producer() { return "TestStringResourceInjectionAppName2"; }

    @Produces
    @ResourceQualifier(lookup = "java:module/ModuleName")
    String moduleName2Producer() { return "TestStringResourceInjectionModuleName2"; }


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
