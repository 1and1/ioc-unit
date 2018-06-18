package com.oneandone.ejbcdiunit.cfganalyzer;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

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
import org.jglue.cdiunit.CdiRunner;
import org.jglue.cdiunit.internal.TypesScanner;
import org.mockito.Mock;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.oneandone.ejbcdiunit.CdiTestConfig;
import com.oneandone.ejbcdiunit.cdiunit.ExcludedClasses;

/**
 * Analyzes the current Testconfiguration of a cdi-unit testclass together with the classpath and an optional TestConfiguration. This is the
 * intermediate step to the creation of a Weld-Container or some other Container able to run the testclass.
 *
 * @author aschoerk
 */
public abstract class TestConfigAnalyzer {

    static boolean weldBefore24 = false;
    private static Logger log = LoggerFactory.getLogger(TestConfigAnalyzer.class);
    protected Set<URL> cdiClasspathEntries = new HashSet<URL>();
    protected Set<String> discoveredClasses = new LinkedHashSet<String>();
    protected Set<String> alternatives = new HashSet<String>();
    protected Set<Class<?>> classesToProcess = new LinkedHashSet<Class<?>>();
    protected Set<Class<?>> classesProcessed = new HashSet<Class<?>>();
    protected Collection<Metadata<? extends Extension>> extensions = new ArrayList<Metadata<? extends Extension>>();
    protected Collection<Metadata<String>> enabledInterceptors = new ArrayList<Metadata<String>>();
    protected Collection<Metadata<String>> enabledDecorators = new ArrayList<Metadata<String>>();
    protected Collection<Metadata<String>> enabledAlternativeStereotypes = new ArrayList<Metadata<String>>();
    protected Set<Class<?>> classesToIgnore;
    List<URL> classpathEntries;
    private boolean analyzeStarted = false;

    private static Constructor metaDataConstructor;


    public TestConfigAnalyzer() {

    }

    protected <T> Metadata<T> createMetadata(T value, String location) {
        try {
            if (metaDataConstructor == null) {
                // If Weld < 2.4, the new package isn't there, so we try the old package.
                // noinspection unchecked
                Class<Metadata<T>> oldClass = (Class<Metadata<T>>) Class.forName("org.jboss.weld.metadata.MetadataImpl");
                Constructor<Metadata<T>> ctor = oldClass.getConstructor(Object.class, String.class);
                metaDataConstructor = ctor;
            }
            return ((Constructor<Metadata<T>>) metaDataConstructor).newInstance(value, location);
        } catch (ReflectiveOperationException e1) {
            throw new RuntimeException(e1);
        }
    }

    public List<URL> getClasspathEntries() {
        return classpathEntries;
    }

    public boolean isAnalyzeStarted() {
        return analyzeStarted;
    }

    public Set<URL> getCdiClasspathEntries() {
        return cdiClasspathEntries;
    }

    public Set<String> getDiscoveredClasses() {
        return discoveredClasses;
    }

    public Set<String> getAlternatives() {
        return alternatives;
    }

    public Set<Class<?>> getClassesToProcess() {
        return classesToProcess;
    }

    public Set<Class<?>> getClassesProcessed() {
        return classesProcessed;
    }

    public Collection<Metadata<? extends Extension>> getExtensions() {
        return extensions;
    }

    public Collection<Metadata<String>> getEnabledInterceptors() {
        return enabledInterceptors;
    }

    public Collection<Metadata<String>> getEnabledDecorators() {
        return enabledDecorators;
    }

    public Collection<Metadata<String>> getEnabledAlternativeStereotypes() {
        return enabledAlternativeStereotypes;
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
        initContainerSpecific(testClass, null);
        transferConfig(config);

        while (!classesToProcess.isEmpty()) {

            Class<?> c = classesToProcess.iterator().next();

            if ((isCdiClass(c) || Extension.class.isAssignableFrom(c))
                    && !classesProcessed.contains(c)
                    && !c.isPrimitive()
                    && !classesToIgnore.contains(c)) {
                classesProcessed.add(c);
                if (!c.isAnnotation()) {
                    discoveredClasses.add(c.getName());
                }
                if (Extension.class.isAssignableFrom(c) && !Modifier.isAbstract(c.getModifiers())) {
                    try {
                        extensions.add(createMetadata((Extension) c.newInstance(), c.getName()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                if (c.isAnnotationPresent(Interceptor.class)) {
                    enabledInterceptors.add(createMetadata(c.getName(), c.getName()));
                }
                if (c.isAnnotationPresent(Decorator.class)) {
                    enabledDecorators.add(createMetadata(c.getName(), c.getName()));
                }

                if (isAlternativeStereotype(c)) {
                    enabledAlternativeStereotypes.add(createMetadata(c.getName(), c.getName()));
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
                for (Method method : c.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(Inject.class) || method.isAnnotationPresent(Produces.class)) {
                        for (Type param : method.getGenericParameterTypes()) {
                            addClassesToProcess(param);
                        }
                        addClassesToProcess(method.getGenericReturnType());

                    }
                }
            }

            classesToProcess.remove(c);
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
        discoveredClasses.add(testClass.getName());
        classesToIgnore = findMockedClassesOfTest(testClass);
        classesToIgnore.addAll(config.getExcludedClasses());
        classesToProcess.add(testClass);
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
            alternatives.add(alternativeClass.getName());
        }
    }

    private void addPackage(Class<?> additionalPackage) throws MalformedURLException {
        final String packageName = additionalPackage.getPackage().getName();
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new TypesScanner())
                .setUrls(additionalPackage.getProtectionDomain().getCodeSource().getLocation()).filterInputsBy(new Predicate<String>() {

                    @Override
                    public boolean apply(String input) {
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

    private void populateCdiClasspathSet() throws IOException {
        ClassLoader classLoader = TestConfigAnalyzer.class.getClassLoader();
        classpathEntries = new ArrayList<URL>(Arrays.asList(((URLClassLoader) classLoader).getURLs()));

        // If this is surefire we need to get the original claspath
        try (JarInputStream firstEntry = new JarInputStream(classpathEntries.get(0).openStream())) {
            Manifest manifest = firstEntry.getManifest();
            if (manifest != null) {
                String classpath = (String) manifest.getMainAttributes().get(Attributes.Name.CLASS_PATH);
                if (classpath != null) {
                    String[] manifestEntries = classpath.split(" ?file:");
                    for (String entry : manifestEntries) {
                        if (entry.length() > 0) {
                            classpathEntries.add(new URL("file:" + entry));
                        }
                    }
                }
            }
        }

        for (URL url : classpathEntries) {
            URLClassLoader cl = new URLClassLoader(new URL[] { url }, null);
            try {

                if (url.getFile().endsWith("/classes/")) {
                    URL webInfBeans = new URL(url, "../../src/main/webapp/WEB-INF/beans.xml");
                    try {
                        webInfBeans.openConnection().connect();;
                        cdiClasspathEntries.add(url);
                    } catch (IOException e) {

                    }
                }
                URL resource = cl.getResource("META-INF/beans.xml");
                boolean cdiUnit = url.equals(CdiRunner.class.getProtectionDomain().getCodeSource().getLocation());
                if (cdiUnit || resource != null || isDirectoryOnClasspath(url)) {
                    cdiClasspathEntries.add(url);
                }

            } finally {
                try {
                    Method method = cl.getClass().getMethod("close");
                    method.invoke(cl);
                } catch (NoSuchMethodException e) {
                    // Ignore, we might be running on Java 6
                } catch (IllegalAccessException e) {
                    // Ignore, we might be running on Java 6
                } catch (InvocationTargetException e) {
                    // Ignore, we might be running on Java 6
                }
            }
        }
        log.trace("CDI classpath classpathEntries discovered:");
        for (URL url : cdiClasspathEntries) {
            log.trace("{}", url);
        }

    }

    private boolean isDirectoryOnClasspath(URL classpathEntry) {
        try {
            return new File(classpathEntry.toURI()).isDirectory();
        } catch (IllegalArgumentException e) {
            // Ignore, thrown by File constructor for unsupported URIs
        } catch (URISyntaxException e) {
            // Ignore, does not denote an URI that points to a directory
        }
        return false;
    }

    private boolean isCdiClass(Class<?> c) {
        if (c.getProtectionDomain().getCodeSource() == null) {
            return false;
        }
        URL location = c.getProtectionDomain().getCodeSource().getLocation();
        boolean isCdi = cdiClasspathEntries.contains(location);
        return isCdi;

    }

    private boolean isAlternativeStereotype(Class<?> c) {
        return c.isAnnotationPresent(Stereotype.class) && c.isAnnotationPresent(Alternative.class);
    }


}
