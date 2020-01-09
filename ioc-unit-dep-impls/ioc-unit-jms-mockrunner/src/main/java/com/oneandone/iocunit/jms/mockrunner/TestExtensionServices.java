package com.oneandone.iocunit.jms.mockrunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.cdi.weldstarter.spi.TestExtensionService;
import com.oneandone.iocunit.ejb.jms.MockRunnerSingletons;

/**
 * @author aschoerk
 */
public class TestExtensionServices implements TestExtensionService {

    static ThreadLocal<Set<Class>> testExtensionServiceData = new ThreadLocal<>();

    private static Logger logger = LoggerFactory.getLogger(TestExtensionServices.class);


    @Override
    public List<Class<?>> testClasses() {
        List<Class<?>> result = new ArrayList<Class<?>>() {

            private static final long serialVersionUID = -519466824492284375L;

            {
                add(MockRunnerSingletons.class);
            }

        };

        return result;
    }


}
