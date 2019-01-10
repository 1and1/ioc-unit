package com.oneandone.ejbcdiunit.ejbs;

import javax.annotation.Resource;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aschoerk
 */
@Stateless
public class ResourceTestEjb {

    Logger log = LoggerFactory.getLogger("ResourceTestEjb");

    @Resource(name = "ResourceTestEjbAppName", lookup = "java:app/AppName", mappedName = "RTEAppName")
    private String appName;

    @Resource(name = "ResourceTestEjbModuleName", lookup = "java:module/ModuleName", mappedName = "RTEModuleName")
    private String moduleName;

    @Resource(lookup = "java:app/AppName")
    private String appName2;

    @Resource(lookup = "java:module/ModuleName")
    private String moduleName2;


    public String ejbAppName() {
        log.info("Appname: appName" + appName);
        return appName;
    }

    public String ejbModuleName() {
        log.info("Modulename: moduleName" + moduleName);
        return moduleName;
    }
    public String ejbAppName2() {
        log.info("Appname: appName2" + appName2);
        return appName2;
    }

    public String ejbModuleName2() {
        log.info("Modulename: moduleName2" + moduleName2);
        return moduleName2;
    }
}
