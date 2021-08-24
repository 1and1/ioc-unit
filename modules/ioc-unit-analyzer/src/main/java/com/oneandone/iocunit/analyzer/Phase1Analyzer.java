package com.oneandone.iocunit.analyzer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.decorator.Decorator;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Specializes;
import javax.enterprise.inject.Stereotype;
import javax.interceptor.Interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.cdi.weldstarter.spi.TestExtensionService;
import com.oneandone.iocunit.analyzer.annotations.EnabledAlternatives;
import com.oneandone.iocunit.analyzer.annotations.ExcludedClasses;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.annotations.SutClasspaths;
import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.analyzer.annotations.SutPackagesDeep;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.analyzer.annotations.TestClasspaths;
import com.oneandone.iocunit.analyzer.annotations.TestPackages;
import com.oneandone.iocunit.analyzer.annotations.TestPackagesDeep;

/**
 * Analyzes the candidates. Works until no new candidates are found.
 * Finds injects. Detects producers.
 * Also detects interceptors, decorators, extension, stereotypes
 * Producers in classes to start found in producerMap. Producers in available classes found in availableProducerMap.
 */
class Phase1Analyzer extends PhasesBase {
    static Logger logger = LoggerFactory.getLogger(Phase1Analyzer.class);

    private ArrayList<Class<?>> newAvailables = new ArrayList<>();
    private Set<Class<?>> handledCandidates = new HashSet<>();
    private Set<URL> testClassPaths = new HashSet<>();

    public Phase1Analyzer(Configuration configuration) {
        super(configuration);
        newAvailables.addAll(configuration.initialAvailables);
    }


    private void findInnerClasses(final Class c) {
        try {
            for (Class innerClass : c.getDeclaredClasses()) {
                if(Modifier.isStatic(innerClass.getModifiers()) && ConfigStatics.mightBeBean(innerClass)) {
                    configuration.available(innerClass);
                    if(configuration.isTestClass(c)) {
                        configuration.testClass(innerClass);
                    }
                    else {
                        configuration.sutClass(innerClass);
                    }
                    findInnerClasses(innerClass);
                    newAvailables.add(innerClass);
                }
            }
        } catch (NoClassDefFoundError e) {
            logger.warn("{} searching innerclasses of {}", e.getMessage(), c.getName());
        }
    }


    private void innerClasses(Class c) {
        ConfigStatics.doInClassAndSuperClasses(c, c1 -> findInnerClasses(c1));
    }


    private void injects(Class c) {
        ConfigStatics.doInClassAndSuperClasses(c, c1 -> {
            InjectFinder injectFinder = new InjectFinder(configuration);
            injectFinder.find(c1);
            for (QualifiedType i : injectFinder.getInjectedTypes())
                configuration.inject(i);
        });
    }

    private void testClassAnnotation(Class<?> c) {
        ConfigStatics.doInClassAndSuperClasses(c, c1 -> {
            TestClasses testClassesL = c1.getAnnotation(TestClasses.class);
            if(testClassesL != null) {
                for (Class<?> testClass : testClassesL.value()) {
                    configuration
                            .testClass(testClass)
                            .candidate(testClass);
                }
            }
        });
    }

    private void sutClassAnnotation(Class<?> c) {
        ConfigStatics.doInClassAndSuperClasses(c, c1 -> {
            SutClasses sutClassesL = c1.getAnnotation(SutClasses.class);
            if(sutClassesL != null) {
                for (Class<?> sutClass : sutClassesL.value()) {
                    configuration
                            .sutClass(sutClass)
                            .candidate(sutClass);
                }
            }
        });
    }


    void extraAnnotations(Class c) {
        final Map<Class<? extends Annotation>, TestExtensionService> extraClassAnnotations =
                configuration.testerExtensionsConfigsFinder.extraClassAnnotations;
        if(extraClassAnnotations.keySet().size() > 0) {
            ConfigStatics.doInClassAndSuperClasses(c, c1 -> {
                extraClassAnnotations.keySet()
                        .stream()
                        .map(a -> c1.getAnnotation((Class<? extends Annotation>) (a)))
                        .filter(res -> res != null)
                        .forEach(res -> extraClassAnnotations.get(((Annotation) res).annotationType())
                                .handleExtraClassAnnotation(res, c1)
                                .forEach(extraAnnotated -> configuration.candidate(extraAnnotated)));
            });
        }
    }

    private void addPackages(Class<?>[] packages, boolean isSut, String filterRegex) throws MalformedURLException {
        for (Class<?> packageClass : packages) {
            Set<Class<?>> tmpClasses = new HashSet<>();
            ClasspathHandler.addPackage(packageClass, tmpClasses, filterRegex);
            addAvailables(isSut, tmpClasses);
        }
    }

    private void addPackagesDeep(Class<?>[] packages, boolean isSut, String filterRegex) throws MalformedURLException {
        for (Class<?> packageClass : packages) {
            Set<Class<?>> tmpClasses = new HashSet<>();
            ClasspathHandler.addPackageDeep(packageClass, tmpClasses, filterRegex);
            addAvailables(isSut, tmpClasses);
        }
    }

    private void addClasspaths(Class<?>[] classpaths, boolean isSut, String filterRegex) throws MalformedURLException {
        for (Class<?> classpathClass : classpaths) {
            Set<Class<?>> tmpClasses = new HashSet<>();
            ClasspathHandler.addClassPath(classpathClass, tmpClasses, filterRegex);
            addAvailables(isSut, tmpClasses);
        }
    }

    boolean isObligatoryAccordingToServices(Class<?> clazz) {
        for (TestExtensionService s : configuration.testerExtensionsConfigsFinder.testExtensionServices) {
            if(s.candidateToStart(clazz)) {
                return true;
            }
        }
        return false;
    }

    private void addAvailables(final boolean isSut, final Set<Class<?>> tmpClasses) {
        for (Class<?> c : tmpClasses) {
            if(c.isInterface() || c.isAnnotation() || Modifier.isAbstract(c.getModifiers())) {
                continue;
            }
            if(!isSut) {
                configuration.testClass(c);
            }
            else {
                configuration.sutClass(c);
            }
            if(isObligatoryAccordingToServices(c) || isObligatoryAccordingToCandidateSigns(c)) {
                configuration.candidate(c);
            }
            else {
                configuration.available(c);
                newAvailables.add(c);
            }
        }
    }

    private boolean isObligatoryAccordingToCandidateSigns(final Class<?> c) {
        return !configuration.isExcluded(c) && !configuration.isCandidate(c) && configuration.isSuTClass(c) &&
               configuration.getCandidateSigns().stream().anyMatch(cs -> cs.isAssignableFrom(c) || cs.isAnnotation() && c.isAnnotationPresent((Class<Annotation>) cs));
    }

    public void extend(final Set<Class<?>> tmpClasses, boolean isSut) {
        for (Class<?> c : tmpClasses) {
            if(c.isInterface() || c.isAnnotation() || Modifier.isAbstract(c.getModifiers())) {
                continue;
            }
            if(isSut) {
                configuration.sutClass(c);
            }
            else {
                configuration.testClass(c);
            }
            if(isObligatoryAccordingToServices(c)) {
                configuration.candidate(c);
            }
            else {
                makeAvailable(c);
            }
        }
    }


    private void packagesAnnotations(Class<?> c) {
        ConfigStatics.doInClassAndSuperClasses(c, c1 -> {
            try {
                SutPackages sutPackages = c1.getAnnotation(SutPackages.class);
                if(sutPackages != null) {
                    addPackages(sutPackages.value(), true, sutPackages.filteringRegex());
                }
                TestPackages testPackages = c1.getAnnotation(TestPackages.class);
                if(testPackages != null) {
                    addPackages(testPackages.value(), false, testPackages.filteringRegex());
                }
                SutPackagesDeep sutPackagesDeep = c1.getAnnotation(SutPackagesDeep.class);
                if(sutPackagesDeep != null) {
                    addPackagesDeep(sutPackagesDeep.value(), true, sutPackagesDeep.filteringRegex());
                }
                TestPackagesDeep testPackagesDeep = c1.getAnnotation(TestPackagesDeep.class);
                if(testPackagesDeep != null) {
                    addPackagesDeep(testPackagesDeep.value(), false, testPackagesDeep.filteringRegex());
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void classpathsAnnotations(Class<?> c) {
        ConfigStatics.doInClassAndSuperClasses(c, c1 -> {
            try {
                SutClasspaths sutClasspaths = c1.getAnnotation(SutClasspaths.class);
                if(sutClasspaths != null) {
                    addClasspaths(sutClasspaths.value(), true, sutClasspaths.filteringRegex());
                }
                TestClasspaths testClasspaths = c1.getAnnotation(TestClasspaths.class);
                if(testClasspaths != null) {
                    addClasspaths(testClasspaths.value(), false, sutClasspaths.filteringRegex());
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void enabledAlternatives(Class<?> c) {
        ConfigStatics.doInClassAndSuperClasses(c, c1 -> {
            EnabledAlternatives enabledAlternativesL = c1.getAnnotation(EnabledAlternatives.class);
            if(enabledAlternativesL != null) {
                for (Class<?> aClass : enabledAlternativesL.value()) {
                    configuration
                            .testClass(aClass)
                            .candidate(aClass)
                            .enabledAlternative(aClass);
                }
            }
        });
    }

    private void excludes(Class<?> c) {
        ConfigStatics.doInClassAndSuperClasses(c, c1 -> {
            ExcludedClasses excludedClassesL = c1.getAnnotation(ExcludedClasses.class);
            if(excludedClassesL != null) {
                for (Class<?> aClass : excludedClassesL.value())
                    configuration.excluded(aClass);
            }
        });
    }

    private void customAnnotations(Class<?> c) {
        ConfigStatics.doInClassAndSuperClasses(c, c1 -> {
            Annotation[] annotations = c1.getAnnotations();
            for (Annotation ann : annotations) {
                final Class<? extends Annotation> annotationType = ann.annotationType();
                for (Annotation annann : annotationType.getAnnotations()) {
                    if(annann.annotationType().getPackage().equals(TestClasses.class.getPackage())) {
                        if(!configuration.isAvailable(annotationType)) {
                            testClassAnnotation(annotationType);
                            classpathsAnnotations(annotationType);
                            sutClassAnnotation(annotationType);
                            packagesAnnotations(annotationType);
                            enabledAlternatives(annotationType);
                            customAnnotations(annotationType);
                            extraAnnotations(annotationType);
                        }
                    }
                }
            }
        });
    }

    private boolean containsProducingAnnotation(final Annotation[] annotations) {
        boolean foundProduces = false;
        boolean foundInject = false;
        for (Annotation ann : annotations) {
            if(ann.annotationType().equals(Produces.class)) {
                foundProduces = true;
            }
            else if(configuration.injectAnnotations.contains(ann.annotationType())) {
                foundInject = true;
            }
        }
        return !foundInject && foundProduces;
    }

    private boolean isStereotype(Annotation ann) {
        for (Annotation subann : ann.annotationType().getAnnotations()) {
            if(subann.annotationType().equals(Stereotype.class)) {
                return true;
            }
        }
        return false;
    }

    private void producerFields(Class c, ProducerMap producerMap) {
        try {
            for (Field f : c.getDeclaredFields()) {
                if(containsProducingAnnotation(f.getAnnotations())) {
                    producerMap.addToProducerMap(new QualifiedType(f));
                }
            }
        } catch (NoClassDefFoundError e) {
            logger.warn("{} analyzing producer fields of {}", e.getMessage(), c.getName());
        }
    }

    private void producerMethods(Class c, ProducerMap producerMap) {
        try {
            for (Method m : c.getDeclaredMethods()) {
                if(containsProducingAnnotation(m.getAnnotations())) {
                    producerMap.addToProducerMap(new QualifiedType(m));
                }
            }
        } catch (NoClassDefFoundError e) {
            logger.warn("{} analyzing producer Methods of {}", e.getMessage(), c.getName());
        }
    }

    private void beanWithoutProducer(final Class<?> c) {
        configuration
                .tobeStarted(c)
                .elseClass(c);
        innerClasses(c);
        injects(c);
        if(configuration.isTestClass(c)) {
            testClassAnnotation(c);
            sutClassAnnotation(c);
            classpathsAnnotations(c);
            packagesAnnotations(c);
            customAnnotations(c);
            enabledAlternatives(c);
            extraAnnotations(c);
            excludes(c);
        }
        specializes(c);
    }

    private void specializes(final Class<?> c) {
        if(c != null && c != Object.class && !c.isInterface()) {
            Specializes specializesL = c.getAnnotation(Specializes.class);
            final Class<?> superclass = c.getSuperclass();
            if(!configuration.getObligatory().contains(superclass)) {
                if(specializesL != null) {
                    if(configuration.isTestClass(c)) {
                        configuration.testClass(superclass);
                    }
                    else {
                        configuration.sutClass(superclass);
                    }
                    configuration.candidate(superclass);
                }
                else {
                    specializes(superclass);
                }
            }
        }
    }

    private void abstractSuperClasses(final Class<?> c) {
        final Class<?> superclass = c.getSuperclass();
        if(!(superclass.equals(Object.class)
             || superclass == null)) {
            addToProducerMap(superclass, configuration.getProducerMap(), false);
            abstractSuperClasses(superclass);
        }
    }

    private QualifiedType addToProducerMap(final Class<?> c, final ProducerMap producerMap) {
        return addToProducerMap(c, producerMap, true);
    }

    private QualifiedType addToProducerMap(final Class<?> c, final ProducerMap producerMap, boolean checkAbstract) {
        final QualifiedType result = new QualifiedType(c, checkAbstract);
        if(!Modifier.isAbstract(c.getModifiers())) {
            producerMap.addToProducerMap(result);
        }
        producerFields(c, producerMap);
        producerMethods(c, producerMap);
        return result;
    }

    boolean work() {
        configuration.setPhase(Configuration.Phase.ANALYZING);
        logger.trace("Phase1Analyzer starting");
        boolean didAnyThing = false;
        do {
            ArrayList<Class<?>> currentCandidates = new ArrayList<>();
            configuration.moveCandidates(currentCandidates);
            for (Class<?> c : currentCandidates) {
                if(handledCandidates.contains(c)) {
                    continue;
                }
                didAnyThing = true;
                logger.trace("evaluating {}", c);
                if(configuration.isExcluded(c)) {
                    logger.info("Excluded {}", c.getName());
                }
                else {
                    if(ConfigStatics.isInterceptingBean(c)) {
                        logger.trace("intercepting {}", c);
                        beanWithoutProducer(c);
                    }
                    else if(ConfigStatics.mightBeBean(c)) {
                        logger.trace("might be Bean {}", c);
                        beanWithoutProducer(c);
                        final ProducerMap producerMap = configuration.getProducerMap();
                        QualifiedType q = addToProducerMap(c, producerMap);
                        if(c.equals(configuration.getTheTestClass()) || q.isAlternative()) {
                            abstractSuperClasses(c);
                        }
                    }
                    else if(ConfigStatics.mightSignCandidate(c)) {
                        if(!configuration.getCandidateSigns().contains(c)) {
                            configuration.getCandidateSigns().add(c);
                            List<Class<?>> toAdd = configuration.getAvailable()
                                    .stream()
                                    .filter(availableClass -> isObligatoryAccordingToCandidateSigns(availableClass))
                                    .collect(Collectors.toList());
                            toAdd.stream().forEach(added -> {
                                newAvailables.remove(added);
                                configuration.candidate(added);
                            });
                            configuration
                                    .tobeStarted(c)
                                    .elseClass(c);
                        }
                    }
                    else if(c.isEnum() || c.isInterface() || c.isArray()) {
                        logger.trace("ignoring {}", c);
                    }
                    else {
                        logger.trace("else but to be started {}", c);
                        configuration
                                .tobeStarted(c)
                                .elseClass(c);
                    }
                }
                handledCandidates.add(c);
            }
        } while (!configuration.emptyCandidates());
        for (Class<?> c : newAvailables) {
            makeAvailable(c);
        }
        newAvailables.clear();
        logger.trace("Phase1Analyzer ready");
        return didAnyThing;

    }

    private void makeAvailable(final Class<?> c) {
        if(configuration.addAvailableInterceptorsAndDecorators) {
            if(c.getAnnotation(Interceptor.class) != null || c.getAnnotation(Decorator.class) != null) {
                logger.info("Flag addAvailableInterceptorsAndDecorator: {}", c);
                configuration.candidate(c);
            }
        }
        if(ConfigStatics.mightBeBean(c)) {
            configuration.available(c);
            addToProducerMap(c, configuration.getAvailableProducerMap());
        }
    }

}
