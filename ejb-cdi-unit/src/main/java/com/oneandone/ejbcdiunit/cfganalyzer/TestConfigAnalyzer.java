package com.oneandone.ejbcdiunit.cfganalyzer;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

import javax.decorator.Decorator;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.interceptor.Interceptor;

import org.jboss.weld.bootstrap.spi.Metadata;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.AdditionalClasspaths;
import org.jglue.cdiunit.AdditionalPackages;
import org.mockito.Mock;
import org.reflections8.ReflectionUtils;
import org.reflections8.Reflections;
import org.reflections8.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.ejbcdiunit.CdiTestConfig;
import com.oneandone.ejbcdiunit.cdiunit.EjbJarClasspath;
import com.oneandone.ejbcdiunit.cdiunit.ExcludedClasses;
import com.oneandone.ejbcdiunit.internal.TypesScanner;

/**
 * Analyzes the current Testconfiguration of a cdi-unit testclass together with the classpath and an optional TestConfiguration. This is the
 * intermediate step to the creation of a Weld-Container or some other Container able to run the testclass.
 *
 * @author aschoerk
 */
public abstract class TestConfigAnalyzer {

    private static Logger log = LoggerFactory.getLogger(TestConfigAnalyzer.class);
    protected final TestConfig testConfig = new TestConfig();
    protected Set<Class<?>> classesToIgnore;
    private Set<Class<?>> classesToProcess = new LinkedHashSet<>();
    private Set<Class<?>> classesProcessed = new HashSet<Class<?>>();
    private boolean analyzeStarted = false;

    private static Constructor metaDataConstructor;
    protected String weldVersion;

    public TestConfigAnalyzer() {

    }

    public TestConfig getTestConfig() {
        return testConfig;
    }

    protected <T> Metadata<T> createMetadata(T value, String location) {
        try {
            return new org.jboss.weld.bootstrap.spi.helpers.MetadataImpl<>(value, location);
        } catch (NoClassDefFoundError e) {
            // MetadataImpl moved to a new package in Weld 2.4, old copy removed in 3.0
            try {
                // If Weld < 2.4, the new package isn't there, so we try the old package.
                // noinspection unchecked
                Class<Metadata<T>> oldClass = (Class<Metadata<T>>) Class.forName("org.jboss.weld.metadata.MetadataImpl");
                Constructor<Metadata<T>> ctor = oldClass.getConstructor(Object.class, String.class);
                return ctor.newInstance(value, location);
            } catch (ReflectiveOperationException e1) {
                throw new RuntimeException(e1);
            }
        }
    }

    public Set<String> getDiscoveredClasses() {
        return testConfig.getDiscoveredClasses();
    }

    public Set<Class<?>> getClassesToProcess() {
        return classesToProcess;
    }

    public Set<Class<?>> getClassesToIgnore() {
        return classesToIgnore;
    }

    private void checkSetAnalyzeStarted() {
        if (analyzeStarted) {
            throw new RuntimeException("Can use Analyzer only once");
        }
        analyzeStarted = true;
    }

    public void analyze(Class<?> testClass) throws IOException {
        analyze(testClass, null, new CdiTestConfig());
    }

    public void analyze(Class<?> testClass, CdiTestConfig config) throws IOException {
        analyze(testClass, null, config);
    }

    public void analyze(Class<?> testClass, Method testMethod, CdiTestConfig config) throws IOException {
        checkSetAnalyzeStarted();
        init(testClass, config);
        populateCdiClasspathSet();
        initContainerSpecific(testClass, testMethod);
        transferConfig(config);

        while (!classesToProcess.isEmpty()) {

            Class<?> c = classesToProcess.iterator().next();

            if ((isCdiClass(c) || Extension.class.isAssignableFrom(c))
                    && !classesProcessed.contains(c)
                    && !c.isPrimitive()
                    && !classesToIgnore.contains(c)) {
                classesProcessed.add(c);
                if (!c.isAnnotation()) {
                    testConfig.getDiscoveredClasses().add(c.getName());
                }
                if (Extension.class.isAssignableFrom(c) && !Modifier.isAbstract(c.getModifiers())) {
                    try {
                        testConfig.getExtensions().add(createMetadata((Extension) c.newInstance(), c.getName()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                if (c.isAnnotationPresent(Interceptor.class)) {
                    testConfig.getEnabledInterceptors().add(createMetadata(c.getName(), c.getName()));
                }
                if (c.isAnnotationPresent(Decorator.class)) {
                    testConfig.getEnabledDecorators().add(createMetadata(c.getName(), c.getName()));
                }

                if (isAlternativeStereotype(c)) {
                    testConfig.getEnabledAlternativeStereotypes().add(createMetadata(c.getName(), c.getName()));
                }


                AdditionalClasses additionalClasses = c.getAnnotation(AdditionalClasses.class);
                if (additionalClasses != null) {
                    for (Class<?> supportClass : additionalClasses.value()) {
                        classesToProcess.add(supportClass);
                    }
                    for (String lateBound : additionalClasses.late()) {
                        try {
                            Class<?> clazz = Class.forName(lateBound);
                            classesToProcess.add(clazz);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }

                AdditionalClasspaths additionalClasspaths = c.getAnnotation(AdditionalClasspaths.class);
                if (additionalClasspaths != null) {
                    for (Class<?> additionalClasspath : additionalClasspaths.value()) {
                        addClassPath(additionalClasspath);
                    }
                }

                EjbJarClasspath ejbJarClasspath = c.getAnnotation(EjbJarClasspath.class);
                if (ejbJarClasspath != null && testConfig.getEjbJarClasspathExample() == null) {
                    testConfig.setEjbJarClasspathExample(ejbJarClasspath.value());
                    if (testConfig.getEjbJarClasspathExample() != null) {
                        final URL path = testConfig.getEjbJarClasspathExample().getProtectionDomain().getCodeSource().getLocation();
                        addDeploymentDescriptor(config, path);
                    }
                }


                AdditionalPackages additionalPackages = c.getAnnotation(AdditionalPackages.class);
                if (additionalPackages != null) {
                    for (Class<?> additionalPackage : additionalPackages.value()) {
                        addPackage(additionalPackage);
                    }
                }

                ActivatedAlternatives alternativeClasses = c.getAnnotation(ActivatedAlternatives.class);
                if (alternativeClasses != null) {
                    for (Class<?> alternativeClass : alternativeClasses.value()) {
                        addAlternative(alternativeClass);
                    }
                }

                ExcludedClasses excludedClasses = c.getAnnotation(ExcludedClasses.class);
                if (excludedClasses != null) {
                    if (belongsTo(c, testClass)) {
                        for (Class<?> excludedClass : excludedClasses.value()) {
                            if (classesProcessed.contains(excludedClass)) {
                                throw new RuntimeException("Trying to exclude already processed class: " + excludedClass);
                            } else {
                                classesToIgnore.add(excludedClass);
                            }
                        }
                    } else {
                        throw new RuntimeException("Trying to exclude in not toplevelclass: " + c);
                    }
                }


                for (Annotation a : c.getAnnotations()) {
                    if (!a.annotationType().getPackage().getName().equals("org.jglue.cdiunit")) {
                        classesToProcess.add(a.annotationType());
                    }
                }

                analyzeClass(c);

            }

            classesToProcess.remove(c);
        }
    }

    private void analyzeClass(final Class<?> c) {
        Type superClass = c.getGenericSuperclass();
        if (superClass != null && superClass != Object.class) {
            addClassesToProcess(superClass);
        }

        for (Field field : c.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class) || field.isAnnotationPresent(Produces.class)) {
                addClassesToProcess(field.getGenericType());
            }
            if (field.getType().equals(Provider.class) || field.getType().equals(Instance.class)) {
                addClassesToProcess(field.getGenericType());
            }
        }

        for (Constructor constructor : c.getDeclaredConstructors()) {
            if (constructor.isAnnotationPresent(Produces.class)) {
                log.warn("invalid produces at constructor {}", constructor.toGenericString());
            }
            if (constructor.isAnnotationPresent(Inject.class)) {
                for (Type param : constructor.getGenericParameterTypes()) {
                    addClassesToProcess(param);
                }
            }
        }

        for (Method method : c.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Inject.class) || method.isAnnotationPresent(Produces.class)) {
                for (Type param : method.getGenericParameterTypes()) {
                    addClassesToProcess(param);
                }
                addClassesToProcess(method.getGenericReturnType());
            }
        }
    }


    private void transferConfig(CdiTestConfig config) throws MalformedURLException {
        classesToProcess.addAll(config.getAdditionalClasses());
        for (Class<?> c : config.getAdditionalClassPathes()) {
            addClassPath(c);
        }
        for (Class<?> c : config.getAdditionalClassPackages()) {
            addPackage(c);
        }
        for (Class<?> c : config.getActivatedAlternatives()) {
            addAlternative(c);
        }
    }

    protected abstract void initContainerSpecific(Class<?> testClass, Method testMethod);

    protected void init(Class<?> testClass, CdiTestConfig config) {
        testConfig.getDiscoveredClasses().add(testClass.getName());
        classesToIgnore = findMockedClassesOfTest(testClass);
        classesToIgnore.addAll(config.getExcludedClasses());
        classesToProcess.add(testClass);
        weldVersion = config.weldVersion;

    }

    private boolean belongsTo(Class<?> c, Class<?> testClass) {
        if (testClass.equals(Object.class))
            return false;
        if (c.equals(testClass))
            return true;
        else {
            return belongsTo(c, testClass.getSuperclass());
        }
    }

    private void addAlternative(Class<?> alternativeClass) {
        classesToProcess.add(alternativeClass);

        if (!isAlternativeStereotype(alternativeClass)) {
            testConfig.getAlternatives().add(createMetadata(alternativeClass.getName(), alternativeClass.getName()));
        }
    }

    private void addPackage(Class<?> additionalPackage) throws MalformedURLException {
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
        classesToProcess.addAll(ReflectionUtils.forNames(
                reflections.getStore().get(TypesScanner.class.getSimpleName()).keySet(),
                new ClassLoader[] { getClass().getClassLoader() }));
    }

    private void addClassPath(Class<?> additionalClasspath) throws MalformedURLException {
        final URL path = additionalClasspath.getProtectionDomain().getCodeSource().getLocation();

        Reflections reflections = new Reflections(new ConfigurationBuilder().setScanners(new TypesScanner())
                .setUrls(path));

        classesToProcess.addAll(ReflectionUtils.forNames(
                reflections.getStore().get(TypesScanner.class.getSimpleName()).keySet(),
                new ClassLoader[] { getClass().getClassLoader() }));

        testConfig.getCdiClasspathEntries().add(path);
    }


    private void addClassesToProcess(Type type) {

        if (type instanceof Class) {
            classesToProcess.add((Class<?>) type);
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType) type;
            classesToProcess.add((Class<?>) ptype.getRawType());
            for (Type arg : ptype.getActualTypeArguments()) {
                addClassesToProcess(arg);
            }
        }
    }

    private Set<Class<?>> findMockedClassesOfTest(Class<?> testClass) {
        Set<Class<?>> mockedClasses = new HashSet<Class<?>>();
        Class<?> actClass = testClass;
        while (!actClass.equals(Object.class)) {
            findMockedClassesOfTest(actClass, mockedClasses);
            actClass = actClass.getSuperclass();
        }
        return mockedClasses;
    }

    private Set<Class<?>> findMockedClassesOfTest(Class<?> testClass, Set<Class<?>> mockedClasses) {

        try {
            for (Field field : testClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(Mock.class)) {
                    Class<?> type = field.getType();
                    mockedClasses.add(type);
                }
            }
        } catch (NoClassDefFoundError e) {

        }

        try {

            for (Field field : testClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(org.easymock.Mock.class)) {
                    Class<?> type = field.getType();
                    mockedClasses.add(type);
                }
            }
        } catch (NoClassDefFoundError e) {

        }
        return mockedClasses;
    }

    private void addDeploymentDescriptor(final CdiTestConfig config, final URL url) throws IOException {
        new EjbJarParser(config, url).invoke();
    }

    private void populateCdiClasspathSet() throws IOException {
        new ClasspathSetPopulator().invoke(testConfig.getCdiClasspathEntries());
    }

    private boolean isCdiClass(Class<?> c) {
        if (c.getProtectionDomain().getCodeSource() == null) {
            return false;
        }
        URL location = c.getProtectionDomain().getCodeSource().getLocation();
        boolean isCdi = testConfig.getCdiClasspathEntries().contains(location);
        return isCdi;

    }

    private boolean isAlternativeStereotype(Class<?> c) {
        return c.isAnnotationPresent(Stereotype.class) && c.isAnnotationPresent(Alternative.class);
    }


}
