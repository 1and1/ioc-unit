package com.oneandone.iocunit.resteasy;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.Extension;
import javax.ws.rs.Path;

import com.oneandone.cdi.weldstarter.CreationalContexts;
import com.oneandone.cdi.weldstarter.spi.TestExtensionService;
import com.oneandone.cdi.weldstarter.spi.WeldStarter;

/**
 * @author aschoerk
 */
public class TestExtensionServices implements TestExtensionService {
    @Override
    public List<Extension> getExtensions() {
        List<Extension> result = new ArrayList<>();
        try {
            if (Path.class.getName() != null)
                result.add(new JaxRsRestEasyTestExtension());
        } catch (NoClassDefFoundError ex) {
            ;
        }

        return result;
    }


    @Override
    public List<Class<?>> testClasses() {
        List<Class<?>> result = new ArrayList<>();
        result.add(RestEasyMockInit.class);
        return result;
    }

    @Override
    public void postStartupAction(final CreationalContexts creationalContexts, final WeldStarter weldStarter) {
        creationalContexts.create(RestEasyMockInit.class, ApplicationScoped.class);
    }
}
