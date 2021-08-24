package com.oneandone.iocunit.mocks;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.spi.Extension;

import org.easymock.EasyMockSupport;
import org.mockito.Mockito;

import com.oneandone.cdi.weldstarter.spi.TestExtensionService;

/**
 * @author aschoerk
 */
public class TestExtensionServices implements TestExtensionService {

    @Override
    public List<Extension> getExtensions() {
        List<Extension> result = new ArrayList<>();
        try {
            if (Mockito.class.getName() != null)
                result.add(new MockitoExtension());
        } catch (NoClassDefFoundError ex) {
            ;
        }
        try {
            if (EasyMockSupport.class.getName() != null)
                result.add(new EasyMockExtension());
        } catch (NoClassDefFoundError ex) {
            ;
        }
        return result;
    }
}
