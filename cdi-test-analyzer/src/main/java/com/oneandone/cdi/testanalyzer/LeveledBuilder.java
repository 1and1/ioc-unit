package com.oneandone.cdi.testanalyzer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.decorator.Decorator;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.spi.Extension;
import javax.interceptor.Interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.cdi.testanalyzer.annotations.EnabledAlternatives;
import com.oneandone.cdi.testanalyzer.annotations.ExcludedClasses;
import com.oneandone.cdi.testanalyzer.annotations.SutClasses;
import com.oneandone.cdi.testanalyzer.annotations.SutClasspaths;
import com.oneandone.cdi.testanalyzer.annotations.SutPackages;
import com.oneandone.cdi.testanalyzer.annotations.TestClasses;
import com.oneandone.cdi.testanalyzer.annotations.TestClasspaths;
import com.oneandone.cdi.testanalyzer.annotations.TestPackages;

/**
 * Helps in building up the testconfiguration.
 *
 * @author aschoerk
 */
class LeveledBuilder {

    Logger log = LoggerFactory.getLogger(this.getClass());

    Set<QualifiedType> injections = new HashSet<>();
    Set<QualifiedType> produces = new HashSet<>();
    ProducerMap producerMap = new ProducerMap();
    ProducerMap alternativeMap = new ProducerMap();
    Set<QualifiedType> newAlternatives = new HashSet<>();
    Collection<ProducerPlugin> producerPlugins = Collections.EMPTY_LIST;
    Set<Class<?>> beansToBeStarted = new HashSet<>(); // these beans must be given to CDI to be started
    Set<Class<?>> beansAvailable = new HashSet<>(); // beans can be used for injects
    Set<Class<?>> enabledAlternatives = new HashSet<>();
    Set<Class<?>> excludedClasses = new HashSet<>();

    List<Class<?>> testClassesToBeEvaluated = new ArrayList<>();
    Set<Class<?>> testClasses = new HashSet<>();
    Set<Class<?>> sutClasses = new HashSet<>();
    List<Class<?>> sutClassesToBeEvaluated = new ArrayList<>();
    Set<Class<?>> testClassesAvailable = new HashSet<>();
    Set<Class<?>> sutClassesAvailable = new HashSet<>();
    Set<Class<?>> foundAlternativeStereotypes = new HashSet<>();
    Set<Class<?>> foundAlternativeClasses = new HashSet<>();
    List<Class<?>> decorators = new ArrayList<>();
    List<Class<?>> interceptors = new ArrayList<>();

    Set<Class<? extends Extension>> extensionClasses = new HashSet<>();
    List<Extension> extensionObjects = new ArrayList<>();
    Set<Class<?>> elseClasses = new HashSet<>();
    Set<QualifiedType> handledInjections = new HashSet<>();
    public final TesterExtensionsConfigsFinder testerExtensionsConfigsFinder;


    public LeveledBuilder(InitialConfiguration cfg, TesterExtensionsConfigsFinder testerExtensionsConfigsFinder) {
        if (cfg.testClass != null) {
            addClass(cfg.testClass, testClasses, testClassesToBeEvaluated);
        }
        this.testerExtensionsConfigsFinder = testerExtensionsConfigsFinder;

        addClasses(testerExtensionsConfigsFinder.initialClasses, testClasses, testClassesToBeEvaluated);
        Method testMethod = cfg.testMethod;
        if (cfg.initialClasses != null) {
            addClasses(cfg.initialClasses, testClasses, testClassesToBeEvaluated);
        }
        if (cfg.testClasses != null) {
            addClasses(cfg.testClasses, testClasses, testClassesToBeEvaluated);
        }
        if (cfg.suTClasses != null) {
            addClasses(cfg.suTClasses, sutClasses, sutClassesToBeEvaluated);
        }
        if (cfg.enabledAlternatives != null) {
            addEnabledAlternatives(cfg.enabledAlternatives);
        }
        // prepare available classes
        // they are not further investigated,
        try {
            if (cfg.suTClasspath != null) {
                addClasspaths(setToArray(cfg.suTClasspath), true);
            }
            if (cfg.testClasspath != null)
                addClasspaths(setToArray(cfg.testClasspath), false);
            if (cfg.suTPackages != null)
                addPackages(setToArray(cfg.suTPackages), true);
            if (cfg.testPackages != null)
                addPackages(setToArray(cfg.suTPackages), false);
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
        if (cfg.excludedClasses != null)
            addExcludedClasses(cfg.excludedClasses);
    }

    private Class<?>[] setToArray(final Set<Class<?>> suTClasspath) {
        return suTClasspath.toArray(new Class<?>[suTClasspath.size()]);
    }

    public LeveledBuilder available(Class c) {
        beansAvailable.add(c);
        addToClassMap(c);
        return this;
    }

    private void addClasses(Iterable<Class<?>> value, Set<Class<?>> classes, Collection<Class<?>> classesToBeEvaluated) {
        for (Class<?> aClass : value) {
            addClass(aClass, classes, classesToBeEvaluated);
        }
    }

    private void addClass(final Class<?> aClass, final Set<Class<?>> classes, final Collection<Class<?>> classesToBeEvaluated) {
        if (!classes.contains(aClass)) {
            classesToBeEvaluated.add(aClass);
            classes.add(aClass);
            addToClassMap(aClass);
        }
    }

    private void addPackages(Class<?>[] packages, boolean isSut) throws MalformedURLException {
        for (Class<?> packageClass : packages) {
            Set<Class<?>> tmpClasses = new HashSet<>();
            ClasspathHandler.addPackage(packageClass, tmpClasses);
            addAvailableClasses(isSut, tmpClasses);
        }
    }

    private void addClasspaths(Class<?>[] classpaths, boolean isSut) throws MalformedURLException {
        for (Class<?> classpathClass : classpaths) {
            Set<Class<?>> tmpClasses = new HashSet<>();
            ClasspathHandler.addClassPath(classpathClass, tmpClasses);
            addAvailableClasses(isSut, tmpClasses);
        }
    }

    private void addAvailableClasses(final boolean isSut, final Set<Class<?>> tmpClasses) {
        for (Class clazz : tmpClasses) {
            if (CdiConfigCreator.mightBeBean(clazz)) {
                available(clazz);
                if (isSut)
                    sutClassesAvailable.add(clazz);
                else
                    testClassesAvailable.add(clazz);
            }
        }
    }

    private void addEnabledAlternatives(Iterable<Class<?>> enabledAlternativesP) {
        for (Class<?> alternative : enabledAlternativesP) {
            this.enabledAlternatives.add(alternative);
            if (alternative.getAnnotation(Alternative.class) == null) {
                boolean found = false;
                for (Method m : alternative.getDeclaredMethods()) {
                    if (m.getAnnotation(Alternative.class) != null) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    for (Field f : alternative.getDeclaredFields()) {
                        if (f.getAnnotation(Alternative.class) != null) {
                            found = true;
                            break;
                        }
                    }

                }
                if (!found) {
                    foundAlternativeClasses.add(alternative);
                } else {
                    if (!testClasses.contains(alternative)) {
                        testClasses.add(alternative);
                        testClassesToBeEvaluated.add(alternative);
                        addToClassMap(alternative);
                    }
                }
            } else {
                if (!testClasses.contains(alternative)) {
                    testClasses.add(alternative);
                    testClassesToBeEvaluated.add(alternative);
                    addToClassMap(alternative);
                }
            }
            addToClassMap(alternative);
        }
    }

    private void addExcludedClasses(Iterable<Class<?>> excludedClassesL) {
        for (Class<?> excl : excludedClassesL) {
            this.excludedClasses.add(excl);
        }
    }

    private void addToClassMap(Class<?> clazz) {
        final QualifiedType q = new QualifiedType(clazz);
        addToProducerMap(q);
    }

    private void addToProducerMap(final QualifiedType q) {
        if (q.isAlternative()) {
            newAlternatives.add(q);
        }
        producerMap.addToProducerMap(q);
    }

    private void verifyAltProducers() {
        for (QualifiedType q : newAlternatives) {
            Class altStereoType = q.getAlternativeStereotype() != null ? q.getAlternativeStereotype().annotationType() : null;
            boolean foundStereotype = false;
            if (altStereoType != null) {
                for (Class c : foundAlternativeStereotypes) {
                    if (altStereoType.getName().equals(c.getName())) {
                        foundStereotype = true;
                        break;
                    }
                }
            }

            if (enabledAlternatives.contains(q.getDeclaringClass()) ||
                    foundStereotype) {
                alternativeMap.addToProducerMap(q);
            }
        }
        newAlternatives.clear();
    }


    private void addDecorator(final Class<?> c) {
        decorators.add(c);

    }

    private void addInterceptor(final Class<?> c) {
        interceptors.add(c);
    }


    public Set<Class<? extends Extension>> getExtensionClasses() {
        return extensionClasses;
    }


    private void findInnerClasses(final Class c, final Set<Class<?>> staticInnerClasses) {
        for (Class innerClass : c.getDeclaredClasses()) {
            if (Modifier.isStatic(innerClass.getModifiers()) && CdiConfigCreator.mightBeBean(innerClass)) {
                staticInnerClasses.add(innerClass);
                addToClassMap(innerClass);
                findInnerClasses(innerClass, staticInnerClasses);
            }
        }
    }

    boolean isTestClass(Class<?> c) {
        if (testClasses.contains(c)) {
            return true;
        } else
            return false;
    }

    boolean isTestClassAvailable(Class<?> c) {
        if (testClassesAvailable.contains(c)) {
            return true;
        } else if (c.getDeclaringClass() != null)
            return isTestClass(c.getDeclaringClass());
        else
            return false;
    }

    boolean isSuTClass(Class<?> c) {
        if (sutClasses.contains(c) || sutClassesAvailable.contains(c)) {
            return true;
        } else if (c.getDeclaringClass() != null)
            return isSuTClass(c.getDeclaringClass());
        else
            return false;
    }

    List<Class<?>> extractToBeEvaluatedClasses() {
        verifyAltProducers();
        List<Class<?>> newToBeEvaluated = new ArrayList<>();
        newToBeEvaluated.addAll(testClassesToBeEvaluated);
        testClassesToBeEvaluated.clear();
        newToBeEvaluated.addAll(sutClassesToBeEvaluated);
        sutClassesToBeEvaluated.clear();
        return newToBeEvaluated;
    }


    LeveledBuilder tobeStarted(Class c) {
        log.info("To be Started: {}", c.getName());
        beansToBeStarted.add(c);
        addToClassMap(c);
        return this;
    }

    LeveledBuilder innerClasses(Class c) {
        return doInClassAndSuperClasses(c, c1 -> findInnerClasses(c1, beansAvailable));
    }

    private LeveledBuilder doInClassAndSuperClasses(final Class<?> c, final ClassHandler classHandler) {
        if (c == null || c.equals(Object.class)) {
            return this;
        } else {
            classHandler.handle(c);
            doInClassAndSuperClasses(c.getSuperclass(), classHandler);
            return this;
        }
    }

    LeveledBuilder injects(Class c) {
        return doInClassAndSuperClasses(c, c1 -> {
            InjectFinder injectFinder = new InjectFinder(testerExtensionsConfigsFinder);
            injectFinder.find(c1);
            injections.addAll(injectFinder.getInjectedTypes());
        });
    }

    LeveledBuilder injectHandled(QualifiedType inject) {
        injections.remove(inject);
        handledInjections.add(inject);
        return this;
    }


    LeveledBuilder producerFields(Class c) {
        for (Field f : c.getDeclaredFields()) {
            if (containsProducingAnnotation(f.getAnnotations())) {
                final QualifiedType q = new QualifiedType(f);
                produces.add(q);
                addToProducerMap(q);
            }
        }
        return this;
    }

    LeveledBuilder producerMethods(Class c) {
        for (Method m : c.getDeclaredMethods()) {
            if (containsProducingAnnotation(m.getAnnotations())) {
                final QualifiedType q = new QualifiedType(m);
                produces.add(q);
                addToProducerMap(q);
            }
        }
        return this;
    }

    private boolean containsProducingAnnotation(final Annotation[] annotations) {
        for (ProducerPlugin producerPlugin : producerPlugins) {
            if (producerPlugin.isProducing(annotations)) {
                extensionClasses.add(producerPlugin.extensionToInstall());
                return true;
            }
        }
        for (Annotation ann : annotations) {
            if (ann.annotationType().equals(Produces.class))
                return true;
        }
        return false;
    }

    LeveledBuilder testClass(Class<?> c) {
        testClasses.add(c);
        testClassesAvailable.remove(c);
        addToClassMap(c);
        return this;
    }

    LeveledBuilder sutClass(Class<?> c) {
        sutClasses.add(c);
        sutClassesAvailable.remove(c);
        addToClassMap(c);
        return this;
    }

    boolean isObligatoryClass(Class<?> c) {
        return testClasses.contains(c) || sutClasses.contains(c);
    }

    LeveledBuilder testClassAnnotation(Class<?> c) {
        return doInClassAndSuperClasses(c, c1 -> {
            TestClasses testClassesL = c1.getAnnotation(TestClasses.class);
            if (testClassesL != null) {
                addClasses(Arrays.asList(testClassesL.value()), this.testClasses, testClassesToBeEvaluated);
            }
        });
    }

    LeveledBuilder sutClassAnnotation(Class<?> c) {
        return doInClassAndSuperClasses(c, c1 -> {
            SutClasses sutClassesL = c1.getAnnotation(SutClasses.class);
            if (sutClassesL != null) {
                addClasses(Arrays.asList(sutClassesL.value()), this.sutClasses, sutClassesToBeEvaluated);
            }
        });
    }


    LeveledBuilder extraAnnotations(Class c) {
        if (this.testerExtensionsConfigsFinder.extraClassAnnotations.keySet().size() > 0) {
            return doInClassAndSuperClasses(c, c1 -> {
                testerExtensionsConfigsFinder.extraClassAnnotations.keySet()
                        .stream()
                        .map(a -> c1.getAnnotation((Class<? extends Annotation>) (a)))
                        .filter(res -> res != null)
                        .forEach(res -> testerExtensionsConfigsFinder.extraClassAnnotations.get(((Annotation) res).annotationType())
                                .handleExtraClassAnnotation(res, c1));
            });
        }
        return this;
    }


    LeveledBuilder packagesAnnotations(Class<?> c) {
        return doInClassAndSuperClasses(c, c1 -> {
            try {
                SutPackages sutPackages = c1.getAnnotation(SutPackages.class);
                if (sutPackages != null) {
                    addPackages(sutPackages.value(), true);
                }
                TestPackages testPackages = c1.getAnnotation(TestPackages.class);
                if (testPackages != null) {
                    addPackages(testPackages.value(), false);
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    LeveledBuilder classpathsAnnotations(Class<?> c) {
        return doInClassAndSuperClasses(c, c1 -> {
            try {
                SutClasspaths sutClasspaths = c1.getAnnotation(SutClasspaths.class);
                if (sutClasspaths != null) {
                    addClasspaths(sutClasspaths.value(), true);
                }
                TestClasspaths testClasspaths = c1.getAnnotation(TestClasspaths.class);
                if (testClasspaths != null) {
                    addClasspaths(testClasspaths.value(), false);
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    LeveledBuilder enabledAlternatives(Class<?> c) {
        return doInClassAndSuperClasses(c, c1 -> {
            EnabledAlternatives enabledAlternativesL = c1.getAnnotation(EnabledAlternatives.class);

            if (enabledAlternativesL != null) {
                addEnabledAlternatives(Arrays.asList(enabledAlternativesL.value()));
            }
        });
    }

    LeveledBuilder excludes(Class<?> c) {
        return doInClassAndSuperClasses(c, c1 -> {
            ExcludedClasses excludedClassesL = c1.getAnnotation(ExcludedClasses.class);
            if (excludedClassesL != null) {
                addExcludedClasses(Arrays.asList(excludedClassesL.value()));
            }
        });
    }

    LeveledBuilder customAnnotations(Class<?> c) {
        return doInClassAndSuperClasses(c, c1 -> {
            Annotation[] annotations = c1.getAnnotations();
            for (Annotation ann : annotations) {
                final Class<? extends Annotation> annotationType = ann.annotationType();
                for (Annotation annann : annotationType.getAnnotations()) {
                    if (annann.annotationType().getPackage().equals(TestClasses.class.getPackage())) {
                        if (!beansAvailable.contains(annotationType)) {
                            testClassAnnotation(annotationType)
                                    .classpathsAnnotations(annotationType)
                                    .sutClassAnnotation(annotationType)
                                    .packagesAnnotations(annotationType)
                                    .enabledAlternatives(annotationType)
                                    .customAnnotations(annotationType)
                                    .extraAnnotations(annotationType);
                        }
                    }
                }
            }
        });
    }

    LeveledBuilder elseClass(Class<?> c) {
        if (CdiConfigCreator.isExtension(c)) {
            extensionClasses.add((Class<? extends Extension>) c);
        } else if (c.getAnnotation(Decorator.class) != null) {
            addDecorator(c);
        } else if (c.getAnnotation(Interceptor.class) != null) {
            addInterceptor(c);
        } else if (c.isAnnotation()) {
            if (c.isAnnotationPresent(Stereotype.class) && c.isAnnotationPresent(Alternative.class)) {
                log.info("Found alternative Stereotype {}", c);
                foundAlternativeStereotypes.add(c);
            } else {
                elseClasses.add(c);
            }
        } else {
            elseClasses.add(c);
        }

        return this;
    }

    boolean isActiveAlternativeStereoType(Annotation c) {
        log.info("Searching for alternative Stereotype {}", c);
        for (Class stereoType : foundAlternativeStereotypes) {
            if (stereoType.getName().equals(c.annotationType().getName())) {
                log.info("Found alternative Stereotype {}", c);
                return true;
            }
        }
        return false;
        // return foundAlternativeStereotypes.contains(c.annotationType());
    }

    boolean isAlternative(Class<?> c) {
        return enabledAlternatives.contains(c);
    }

    /**
     * create a LeveledBuilder which can be used to find producers for left over injects. An extra builder is used, to be able to select before
     * deciding which classes to use.
     * 
     * @return
     */
    public LeveledBuilder producerCandidates() {
        Set<Class<?>> tmp = new HashSet<>();
        tmp.addAll(beansAvailable);
        tmp.removeAll(beansToBeStarted);
        LeveledBuilder result = new LeveledBuilder(new InitialConfiguration(), testerExtensionsConfigsFinder);
        for (Class<?> c : tmp) {
            result.available(c); // necessary? already is available
            result.producerFields(c);
            result.producerMethods(c);
        }
        return result;
    }


    interface ClassHandler {
        void handle(Class<?> c);
    }

}
