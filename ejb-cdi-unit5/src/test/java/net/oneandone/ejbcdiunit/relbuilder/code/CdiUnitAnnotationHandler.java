package net.oneandone.ejbcdiunit.relbuilder.code;

import java.net.URL;
import java.util.function.Predicate;

import javax.enterprise.inject.Alternative;

import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.AdditionalClasspaths;
import org.jglue.cdiunit.AdditionalPackages;
import org.reflections8.ReflectionUtils;
import org.reflections8.Reflections;
import org.reflections8.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.ejbcdiunit.cdiunit.EjbJarClasspath;
import com.oneandone.ejbcdiunit.cdiunit.ExcludedClasses;
import com.oneandone.ejbcdiunit.internal.TypesScanner;

public class CdiUnitAnnotationHandler {
    Logger logger = LoggerFactory.getLogger(CdiUnitAnnotationHandler.class);
    private final RelationFactory relFactory;
    private final Rels.BeanClassRel parent;

    public CdiUnitAnnotationHandler(RelationFactory relFactory, final Rels.BeanClassRel parent) {
        this.relFactory = relFactory;
        this.parent = parent;
    }

    void handleAdditionalClassAnnotations(final ClassWrapper c) throws CdiRelBuilder.AnalyzerException {
        if (c.isAnnotationPresent(AdditionalClasses.class)) {
            addAdditionalClasses(c);
        }
        if (c.isAnnotationPresent(ActivatedAlternatives.class)) {
            addActivatedAlternatives(c);
        }

        if (c.isAnnotationPresent(AdditionalPackages.class)) {
            addAdditionalPackages(c);
        }
        if (c.isAnnotationPresent(AdditionalClasspaths.class)) {
            addAdditionalClasspaths(c);
        }
        if (c.isAnnotationPresent(EjbJarClasspath.class)) {
            addEjbJarClasspath(c);
        }
        if (c.isAnnotationPresent(ExcludedClasses.class)) {
            addExcludedClasses(c);
        }
    }

    private void addEjbJarClasspath(final ClassWrapper c) {
        EjbJarClasspath ann = c.getAnnotation(EjbJarClasspath.class);
        Rels.EjbClasspathRel additionalPackageRel = new Rels.EjbClasspathRel(ann, parent);
    }

    private void addExcludedClasses(final ClassWrapper c) {
        ExcludedClasses ann = c.getAnnotation(ExcludedClasses.class);
        Rels.ExcludedClassesRel additionalPackageRel = new Rels.ExcludedClassesRel(ann, parent);
    }

    private void addAdditionalPackages(final ClassWrapper c) throws CdiRelBuilder.AnalyzerException {
        AdditionalPackages ann = c.getAnnotation(AdditionalPackages.class);
        for (Class<?> additionalPackage : ann.value()) {
            Rels.AdditionalPackageRel additionalPackageRel = new Rels.AdditionalPackageRel(ann, parent);
            final String packageName = additionalPackage.getPackage().getName();
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .setScanners(new TypesScanner())
                    .setUrls(additionalPackage.getProtectionDomain().getCodeSource().getLocation()).filterInputsBy(new Predicate<String>() {
                        @Override
                        public boolean test(String input) {
                            return input.startsWith(packageName)
                                    && !input.substring(packageName.length() + 1, input.length() - 6).contains(".");

                        }
                    }));
            for (Class classPathClass : ReflectionUtils.forNames(
                    reflections.getStore().get(TypesScanner.class.getSimpleName()).keySet(),
                    new ClassLoader[] { getClass().getClassLoader() })) {
                relFactory.createBeanFromClass(additionalPackageRel, classPathClass);
            }
            if (additionalPackageRel.size() == 0) {
                parent.remove(additionalPackageRel);
            }

        }

    }

    private void addAdditionalClasspaths(final ClassWrapper c) throws CdiRelBuilder.AnalyzerException {
        AdditionalClasspaths ann = c.getAnnotation(AdditionalClasspaths.class);
        for (Class<?> additionalClasspathClass : ann.value()) {
            Rels.AdditionalClasspathRel additionalClassPathRel = new Rels.AdditionalClasspathRel(ann, parent);
            final URL path = additionalClasspathClass.getProtectionDomain().getCodeSource().getLocation();

            Reflections reflections = new Reflections(new ConfigurationBuilder().setScanners(new TypesScanner())
                    .setUrls(path));

            for (Class classPathClass : ReflectionUtils.forNames(
                    reflections.getStore().get(TypesScanner.class.getSimpleName()).keySet(),
                    new ClassLoader[] { getClass().getClassLoader() })) {
                relFactory.createBeanFromClass(additionalClassPathRel, classPathClass);
            }
        }

    }

    private void addActivatedAlternatives(final ClassWrapper c) throws CdiRelBuilder.AnalyzerException {
        ActivatedAlternatives ann = c.getAnnotation(ActivatedAlternatives.class);
        Rels.ActivatedAlternativesRel additionalClassesRel = new Rels.ActivatedAlternativesRel(ann, parent);

        for (Class clazz : ann.value()) {
            if (clazz.getAnnotation(Alternative.class) == null) {
                logger.warn("ActivatedAlternative without Annotation \"Alternative\" {}", clazz.getSimpleName());

            }
            relFactory.createBeanFromClass(additionalClassesRel, clazz);
        }
    }

    private void addAdditionalClasses(final ClassWrapper c) throws CdiRelBuilder.AnalyzerException {
        AdditionalClasses ann = c.getAnnotation(AdditionalClasses.class);
        Rels.AdditionalClassesRel additionalClassesRel = new Rels.AdditionalClassesRel(ann, parent);

        for (Class additionalClass : ann.value()) {
            relFactory.createBeanFromClass(additionalClassesRel, additionalClass);
        }
        for (String lateBound : ann.late()) {
            try {
                Class<?> clazz = Class.forName(lateBound);
                relFactory.createBeanFromClass(additionalClassesRel, clazz);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        }
    }
}