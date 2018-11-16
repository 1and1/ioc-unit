package com.oneandone.ejbcdiunit.cfganalyzer;

import static com.oneandone.cdi.testanalyzer.ClasspathHandler.addClassPath;
import static com.oneandone.cdi.testanalyzer.ClasspathHandler.addPackage;

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

import javax.decorator.Decorator;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.interceptor.Interceptor;

import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.AdditionalClasspaths;
import org.jglue.cdiunit.AdditionalPackages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.ejbcdiunit.CdiTestConfig;
import com.oneandone.ejbcdiunit.cdiunit.EjbJarClasspath;
import com.oneandone.ejbcdiunit.cdiunit.ExcludedClasses;

/**
 * Analyzes the current Testconfiguration of a cdi-unit testclass together with the classpath and an optional TestConfiguration. This is the
 * intermediate step to the creation of a Weld-Container or some other Container able to run the testclass.
 *
 * @author aschoerk
 */
public class TestConfigAnalyzer {

    private static Logger log = LoggerFactory.getLogger(TestConfigAnalyzer.class);
    protected CdiTestConfig testConfig = null;
    protected Set<Class<?>> classesToIgnore = new LinkedHashSet<>();
    private Set<Class<?>> classesToProcess = new LinkedHashSet<>();
    private Set<Class<?>> classesProcessed = new HashSet<Class<?>>();
    private boolean analyzeStarted = false;

    public TestConfigAnalyzer() {

    }

    public CdiTestConfig getTestConfig() {
        return testConfig;
    }


    public Set<Class<?>> getClassesToProcess() {
        return classesToProcess;
    }

    private void checkSetAnalyzeStarted() {
        if (analyzeStarted) {
            throw new RuntimeException("Can use Analyzer only once");
        }
        analyzeStarted = true;
    }

    public void analyze(CdiTestConfig config) throws IOException {
        checkSetAnalyzeStarted();
        this.testConfig = config;
        new TestConfigInitializer(config, classesToProcess, classesToIgnore)
                .initForAnalyzer();
        transferInitialClassesToAddConfig(config);

        while (!classesToProcess.isEmpty()) {

            Class<?> c = classesToProcess.iterator().next();

            if ((isCdiClass(c) || Extension.class.isAssignableFrom(c))
                    && !classesProcessed.contains(c)
                    && !c.isPrimitive()
                    && !classesToIgnore.contains(c)) {
                classesProcessed.add(c);
                evaluateClassAttributes(c);


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
                        addClassPath(additionalClasspath, classesToProcess, testConfig.getClasspathEntries());
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
                        addPackage(additionalPackage, classesToProcess);
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
                    if (belongsTo(c, config.getTestClass())) {
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

    private void evaluateClassAttributes(final Class<?> c) {
        if (!c.isAnnotation()) {
            testConfig.getDiscoveredClasses().add(c.getName());
        }
        if (Extension.class.isAssignableFrom(c) && !Modifier.isAbstract(c.getModifiers())) {
            try {
                testConfig.getExtensions().add((Extension) (c.newInstance()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (c.isAnnotationPresent(Interceptor.class)) {
            testConfig.getEnabledInterceptors().add(c);
        }
        if (c.isAnnotationPresent(Decorator.class)) {
            testConfig.getEnabledDecorators().add(c);
        }

        if (isAlternativeStereotype(c)) {
            testConfig.getEnabledAlternativeStereotypes().add(c.getName());
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


    /**
     * extends abstract description of set of classes to real classes
     * 
     * @param config
     * @throws MalformedURLException
     */
    private void transferInitialClassesToAddConfig(CdiTestConfig config) throws MalformedURLException {
        classesToProcess.addAll(config.getAdditionalClasses());
        for (Class<?> c : config.getAdditionalClassPathes()) {
            addClassPath(c, classesToProcess, config.getClasspathEntries());
        }
        for (Class<?> c : config.getAdditionalClassPackages()) {
            addPackage(c, classesToProcess);
        }
        for (Class<?> c : config.getActivatedAlternatives()) {
            addAlternative(c);
        }
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
            testConfig.getAlternatives().add(alternativeClass);
        }
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

    private void addDeploymentDescriptor(final CdiTestConfig config, final URL url) throws IOException {
        new EjbJarParser(config, url).invoke();
    }

    private boolean isCdiClass(Class<?> c) {
        if (c.getProtectionDomain().getCodeSource() == null) {
            return false;
        }
        URL location = c.getProtectionDomain().getCodeSource().getLocation();
        boolean isCdi = testConfig.getClasspathEntries().contains(location);
        return isCdi;
    }

    public static boolean isAlternativeStereotype(Class<?> c) {
        return c.isAnnotationPresent(Stereotype.class) && c.isAnnotationPresent(Alternative.class);
    }


}
