package com.oneandone.iocunit.resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.spi.Extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.cdi.weldstarter.spi.TestExtensionService;

/**
 * @author aschoerk
 */
public class TestExtensionServices implements TestExtensionService {
    static ThreadLocal<Set<Class>> testExtensionServiceData = new ThreadLocal<>();

    private static Logger logger = LoggerFactory.getLogger(TestExtensionServices.class);

    @Override
    public void initAnalyze() {
        if(testExtensionServiceData.get() == null) {
            testExtensionServiceData.set(new HashSet<>());
        }
    }

    @Override
    public List<Extension> getExtensions() {
        List<Extension> result = new ArrayList<>();
        try {
            result.add(new ResourceExtension());
        } catch (NoClassDefFoundError ex) {
            ;
        }

        return result;
    }

}
