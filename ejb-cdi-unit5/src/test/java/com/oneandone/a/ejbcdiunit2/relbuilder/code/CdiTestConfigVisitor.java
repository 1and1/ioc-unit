package com.oneandone.a.ejbcdiunit2.relbuilder.code;

import static com.oneandone.ejbcdiunit.cfganalyzer.CdiMetaDataCreator.createMetadata;

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
    public Object visit(final Rels.ProducerFieldRel producerFieldRel, final Object p) {
        return super.visit(producerFieldRel, p);
    }

    @Override
    public Object visit(final Rels.ProducerMethodRel producerMethodRel, final Object p) {
        return super.visit(producerMethodRel, p);
    }

    @Override
    public Object visit(final Rels.InjectedFieldRel injectedFieldRel, final Object p) {
        cdiTestConfig.getAdditionalClasses().add(injectedFieldRel.affectedClass.getBaseclass());
        return super.visit(injectedFieldRel, p);
    }

    @Override
    public Object visit(final Rels.BeanClassRel beanClassRel, final Object p) {
        cdiTestConfig.getAdditionalClasses().add(beanClassRel.affectedClass.getBaseclass());
        return super.visit(beanClassRel, p);
    }

    @Override
    public Object visit(final Rels.SimpleClassRel simpleClassRel, final Object p) {
        return super.visit(simpleClassRel, p);
    }

    @Override
    public Object visit(final Rels.ConstructorInjectRel constructorInjectRel, final Object p) {
        return super.visit(constructorInjectRel, p);
    }

    @Override
    public Object visit(final Rels.InjectedParameterRel injectedParameterRel, final Object p) {
        cdiTestConfig.getAdditionalClasses().add(injectedParameterRel.affectedClass.getBaseclass());
        return super.visit(injectedParameterRel, p);
    }

    @Override
    public Object visit(final Rels.RootRel rootRel, final Object p) {
        return super.visit(rootRel, p);
    }

    @Override
    public Object visit(final Rels.AdditionalClasspathRel additionalClasspathRel, final Object p) {
        cdiTestConfig.getAdditionalClassPathes().add(additionalClasspathRel.affectedClass.getBaseclass());
        return super.visit(additionalClasspathRel, p);
    }

    @Override
    public Object visit(final Rels.AdditionalClassesRel additionalClassesRel, final Object p) {
        cdiTestConfig.getAdditionalClasses().add(additionalClassesRel.affectedClass.getBaseclass());
        return super.visit(additionalClassesRel, p);
    }

    @Override
    public Object visit(final Rels.AdditionalPackageRel additionalPackageRel, final Object p) {
        cdiTestConfig.getAdditionalClassPackages().add(additionalPackageRel.affectedClass.getBaseclass());
        return super.visit(additionalPackageRel, p);
    }

    @Override
    public Object visit(final Rels.ExcludedClassesRel excludedClassesRel, final Object p) {
        cdiTestConfig.getExcludedClasses().add(excludedClassesRel.affectedClass.getBaseclass());
        return super.visit(excludedClassesRel, p);
    }

    @Override
    public Object visit(final Rels.EjbClasspathRel ejbClasspathRel, final Object p) {
        return super.visit(ejbClasspathRel, p);
    }

    @Override
    public Object visit(final Rels.ActivatedAlternativesRel activatedAlternativesRel, final Object p) {
        cdiTestConfig.getActivatedAlternatives().add(activatedAlternativesRel.affectedClass.getBaseclass());
        return super.visit(activatedAlternativesRel, p);
    }

    private void evaluateClassAttributes(final Class<?> c) {
        if (!c.isAnnotation()) {
            cdiTestConfig.getDiscoveredClasses().add(c.getName());
        }
        if (Extension.class.isAssignableFrom(c) && !Modifier.isAbstract(c.getModifiers())) {
            try {
                cdiTestConfig.getExtensions().add(createMetadata((Extension) c.newInstance(), c.getName()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (c.isAnnotationPresent(Interceptor.class)) {
            cdiTestConfig.getEnabledInterceptors().add(createMetadata(c.getName(), c.getName()));
        }
        if (c.isAnnotationPresent(Decorator.class)) {
            cdiTestConfig.getEnabledDecorators().add(createMetadata(c.getName(), c.getName()));
        }

        if (TestConfigAnalyzer.isAlternativeStereotype(c)) {
            cdiTestConfig.getEnabledAlternativeStereotypes().add(createMetadata(c.getName(), c.getName()));
        }
    }
}
