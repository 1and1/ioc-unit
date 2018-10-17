package net.oneandone.ejbcdiunit.relbuilder.code;

import java.lang.reflect.Modifier;

import javax.decorator.Decorator;
import javax.enterprise.inject.spi.Extension;
import javax.interceptor.Interceptor;

import com.oneandone.ejbcdiunit.CdiTestConfig;
import com.oneandone.ejbcdiunit.cfganalyzer.TestConfigAnalyzer;

/**
 * @author aschoerk
 */
public class CdiTestConfigVisitor extends AllRelVisitor {
    private final CdiTestConfig cdiTestConfig;

    public CdiTestConfigVisitor(CdiTestConfig cdiTestConfig) {
        this.cdiTestConfig = cdiTestConfig;
    }

    @Override
    public Object visit(final CdiRelBuilder.ProducerFieldRel producerFieldRel, final Object p) {
        return super.visit(producerFieldRel, p);
    }

    @Override
    public Object visit(final CdiRelBuilder.ProducerMethodRel producerMethodRel, final Object p) {
        return super.visit(producerMethodRel, p);
    }

    @Override
    public Object visit(final CdiRelBuilder.InjectedFieldRel injectedFieldRel, final Object p) {
        cdiTestConfig.getAdditionalClasses().add(injectedFieldRel.affectedClass.getBaseclass());
        return super.visit(injectedFieldRel, p);
    }

    @Override
    public Object visit(final CdiRelBuilder.BeanClassRel beanClassRel, final Object p) {
        cdiTestConfig.getAdditionalClasses().add(beanClassRel.affectedClass.getBaseclass());
        return super.visit(beanClassRel, p);
    }

    @Override
    public Object visit(final CdiRelBuilder.SimpleClassRel simpleClassRel, final Object p) {
        return super.visit(simpleClassRel, p);
    }

    @Override
    public Object visit(final CdiRelBuilder.ConstructorInjectRel constructorInjectRel, final Object p) {
        return super.visit(constructorInjectRel, p);
    }

    @Override
    public Object visit(final CdiRelBuilder.InjectedParameterRel injectedParameterRel, final Object p) {
        cdiTestConfig.getAdditionalClasses().add(injectedParameterRel.affectedClass.getBaseclass());
        return super.visit(injectedParameterRel, p);
    }

    @Override
    public Object visit(final CdiRelBuilder.RootRel rootRel, final Object p) {
        return super.visit(rootRel, p);
    }

    @Override
    public Object visit(final CdiRelBuilder.AdditionalClasspathRel additionalClasspathRel, final Object p) {
        cdiTestConfig.getAdditionalClassPathes().add(additionalClasspathRel.affectedClass.getBaseclass());
        return super.visit(additionalClasspathRel, p);
    }

    @Override
    public Object visit(final CdiRelBuilder.AdditionalClassesRel additionalClassesRel, final Object p) {
        cdiTestConfig.getAdditionalClasses().add(additionalClassesRel.affectedClass.getBaseclass());
        return super.visit(additionalClassesRel, p);
    }

    @Override
    public Object visit(final CdiRelBuilder.AdditionalPackageRel additionalPackageRel, final Object p) {
        cdiTestConfig.getAdditionalClassPackages().add(additionalPackageRel.affectedClass.getBaseclass());
        return super.visit(additionalPackageRel, p);
    }

    @Override
    public Object visit(final CdiRelBuilder.ExcludedClassesRel excludedClassesRel, final Object p) {
        cdiTestConfig.getExcludedClasses().add(excludedClassesRel.affectedClass.getBaseclass());
        return super.visit(excludedClassesRel, p);
    }

    @Override
    public Object visit(final CdiRelBuilder.EjbClasspathRel ejbClasspathRel, final Object p) {
        return super.visit(ejbClasspathRel, p);
    }

    @Override
    public Object visit(final CdiRelBuilder.ActivatedAlternativesRel activatedAlternativesRel, final Object p) {
        cdiTestConfig.getActivatedAlternatives().add(activatedAlternativesRel.affectedClass.getBaseclass());
        return super.visit(activatedAlternativesRel, p);
    }

    private void evaluateClassAttributes(final Class<?> c) {
        if (!c.isAnnotation()) {
            cdiTestConfig.getDiscoveredClasses().add(c.getName());
        }
        if (Extension.class.isAssignableFrom(c) && !Modifier.isAbstract(c.getModifiers())) {
            try {
                cdiTestConfig.getExtensions().add(TestConfigAnalyzer.createMetadata((Extension) c.newInstance(), c.getName()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (c.isAnnotationPresent(Interceptor.class)) {
            cdiTestConfig.getEnabledInterceptors().add(TestConfigAnalyzer.createMetadata(c.getName(), c.getName()));
        }
        if (c.isAnnotationPresent(Decorator.class)) {
            cdiTestConfig.getEnabledDecorators().add(TestConfigAnalyzer.createMetadata(c.getName(), c.getName()));
        }

        if (TestConfigAnalyzer.isAlternativeStereotype(c)) {
            cdiTestConfig.getEnabledAlternativeStereotypes().add(TestConfigAnalyzer.createMetadata(c.getName(), c.getName()));
        }
    }
}
