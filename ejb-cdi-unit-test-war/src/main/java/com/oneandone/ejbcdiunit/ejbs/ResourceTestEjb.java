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

    public String ejbAppName() {
        log.info("Appname: appName" + appName);
        return appName;
    }

    public String ejbModuleName() {
        log.info("Modulename: moduleName" + moduleName);
        return moduleName;
    }
}
