package net.oneandone.example;

import java.lang.reflect.Method;

import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.metadata.MetadataImpl;
import org.jglue.cdiunit.internal.ProducerConfigExtension;

import com.oneandone.ejbcdiunit.cfganalyzer.TestConfigAnalyzer;

/**
 * @author aschoerk
 */
public class ShrinkwrapAnalyzer extends TestConfigAnalyzer {
    @Override
    protected void initContainerSpecific(Class<?> testClass, Method testMethod) {
        extensions.add(new MetadataImpl<Extension>(new TestScopeExtension(testClass), TestScopeExtension.class.getName()));
        if (testMethod != null) {
            extensions.add(new MetadataImpl<Extension>(new ProducerConfigExtension(testMethod), ProducerConfigExtension.class.getName()));
        }
    }
}
