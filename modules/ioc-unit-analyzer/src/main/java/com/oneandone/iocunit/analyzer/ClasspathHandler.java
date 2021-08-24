package com.oneandone.iocunit.analyzer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;

/**
 * @author aschoerk
 */
public class ClasspathHandler {

    private final static String TYPES_SCANNER_NAME = TypesScanner.class.getSimpleName();
    private final static String SUPERTYPE_NAME = Object.class.getSimpleName();

    /**
     * try to resolve all given string representation of types to a list of java types
     */
    public static <T> Set<Class<? extends T>> forNames(final Collection<String> classes, String filterRegex, ClassLoader... classLoaders) {
        Logger tmplog = Reflections.log;
        Reflections.log = null;
        try {
            Set<Class<? extends T>> tmpRes = ReflectionUtils.forNames(classes, classLoaders);
            if(filterRegex == null || filterRegex.isEmpty()) {
                return tmpRes;
            }
            else {
                Pattern pattern = Pattern.compile(filterRegex);
                return tmpRes.stream().filter(c -> pattern.asPredicate().test(c.getName())).collect(Collectors.toSet());
            }
        } finally {
            Reflections.log = tmplog;
        }
    }

    public static void addPackage(Class<?> additionalPackage, Set<Class<?>> classesToProcess, String filterRegex) throws MalformedURLException {
        final String packageName = additionalPackage.getPackage().getName();
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new TypesScanner())
                .setUrls(additionalPackage.getProtectionDomain().getCodeSource().getLocation())
                .filterInputsBy(new Predicate<String>() {
                    @Override
                    public boolean test(String input) {
                        final String inputR = input.replace('/', '.').replace('\\', '.');
                        if(inputR.startsWith(packageName)) {
                            final String stringAfterPackage =
                                    inputR.substring(packageName.length() + 1, inputR.length() - ".class".length());
                            return !stringAfterPackage.contains(".");
                        }
                        return false;
                    }
                }));
        classesToProcess.addAll(forNames(reflections.getStore().get(TYPES_SCANNER_NAME, SUPERTYPE_NAME), filterRegex,
                new ClassLoader[]{ClasspathHandler.class.getClassLoader()}));
    }

    public static void addClassPath(Class<?> additionalClasspath, Set<Class<?>> classesToProcess, String filterRegex) throws MalformedURLException {
        addClassPath(additionalClasspath, classesToProcess, null, filterRegex);
    }


    public static void addClassPath(Class<?> additionalClasspath,
                                    Set<Class<?>> classesToProcess,
                                    Set<URL> classpathEntries, String filterRegex)
            throws MalformedURLException {
        final URL path = additionalClasspath.getProtectionDomain().getCodeSource().getLocation();

        Reflections reflections = new Reflections(new ConfigurationBuilder().setScanners(new TypesScanner())
                .setUrls(path));

        classesToProcess.addAll(forNames(reflections.getStore().get(TYPES_SCANNER_NAME, SUPERTYPE_NAME), filterRegex,
                new ClassLoader[]{ClasspathHandler.class.getClassLoader()}));

        if(classpathEntries != null) {
            classpathEntries.add(path);
        }
    }

    public static URL getPath(Class<?> clazz) {
        return clazz.getProtectionDomain().getCodeSource().getLocation();
    }

    public static void addPackageDeep(final Class<?> packageClass, final Set<Class<?>> tmpClasses, String filterRegex) {
        final String packageName = packageClass.getPackage().getName();
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new TypesScanner())
                .setUrls(packageClass.getProtectionDomain().getCodeSource().getLocation())
                .filterInputsBy(new Predicate<String>() {
                    @Override
                    public boolean test(String input) {
                        final String inputR = input.replace('/', '.').replace('\\', '.');
                        if(inputR.startsWith(packageName)) {
                            return inputR.substring(packageName.length(), packageName.length() + 1).equals(".");
                        }
                        return false;
                    }
                }));
        tmpClasses.addAll(forNames(reflections.getStore().get(TYPES_SCANNER_NAME, SUPERTYPE_NAME), filterRegex,
                new ClassLoader[]{ClasspathHandler.class.getClassLoader()}));
    }
}
