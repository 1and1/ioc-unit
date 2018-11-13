package com.oneandone.cdi.testanalyzer;

import org.junit.Test;

import javax.enterprise.inject.spi.Extension;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class TestMockito {

    @Test
    public void testMockito() {
        CdiConfigCreator cdiConfigCreator = new CdiConfigCreator();
        List<ProducerPlugin> producerPlugins = new ArrayList<>();
        producerPlugins.add(new ProducerPlugin() {
            @Override
            public boolean isProducing(Annotation[] annotations) {
                for (Annotation ann: annotations) {
                    if (ann.annotationType().equals(org.mockito.Mock.class)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public Class<? extends Extension> extensionToInstall() {
                return com.oneandone.cdi.mocks.MockitoExtension.class;
            }
        });

    }
}
