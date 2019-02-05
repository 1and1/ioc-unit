package com.oneandone.iocunit.analyzer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.function.Predicate;

import org.reflections8.ReflectionUtils;
import org.reflections8.Reflections;
import org.reflections8.util.ConfigurationBuilder;

/**
 * @author aschoerk
 */
public class ClasspathHandler {

    public static void addPackage(Class<?> additionalPackage, Set<Class<?>> classesToProcess) throws MalformedURLException {
        final String packageName = additionalPackage.getPackage().getName();
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new TypesScanner())
                .setUrls(additionalPackage.getProtectionDomain().getCodeSource().getLocation())
                .filterInputsBy(new Predicate<String>() {
                    @Override
                    public boolean test(String input) {
                        final String inputR = input.replace('/', '.').replace('\\', '.');
                        if (inputR.startsWith(packageName)) {
                            final String stringAfterPackage =
                                    inputR.substring(packageName.length() + 1, inputR.length() - ".class".length());
                            return !stringAfterPackage.contains(".");
                        }
                        return false;
                    }
                }));
        classesToProcess.addAll(ReflectionUtils.forNames(
                reflections.getStore().get(TypesScanner.class.getSimpleName()).keySet(),
                new ClassLoader[] { ClasspathHandler.class.getClassLoader() }));
    }

    public static void addClassPath(Class<?> additionalClasspath, Set<Class<?>> classesToProcess) throws MalformedURLException {
        addClassPath(additionalClasspath, classesToProcess, null);
    }


    public static void addClassPath(Class<?> additionalClasspath,
                                    Set<Class<?>> classesToProcess,
                                    Set<URL> classpathEntries)
            throws MalformedURLException {
        final URL path = additionalClasspath.getProtectionDomain().getCodeSource().getLocation();

        Reflections reflections = new Reflections(new ConfigurationBuilder().setScanners(new TypesScanner())
                .setUrls(path));

        classesToProcess.addAll(ReflectionUtils.forNames(
                reflections.getStore().get(TypesScanner.class.getSimpleName()).keySet(),
                new ClassLoader[] { ClasspathHandler.class.getClassLoader() }));

        if (classpathEntries != null)
            classpathEntries.add(path);
    }

    public static URL getPath(Class<?> clazz) {
        return clazz.getProtectionDomain().getCodeSource().getLocation();
    }

    public static void addPackageDeep(final Class<?> packageClass, final Set<Class<?>> tmpClasses) {
        final String packageName = packageClass.getPackage().getName();
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new TypesScanner())
                .setUrls(packageClass.getProtectionDomain().getCodeSource().getLocation())
                .filterInputsBy(new Predicate<String>() {
                    @Override
                    public boolean test(String input) {
                        final String inputR = input.replace('/', '.').replace('\\', '.');
                        if (inputR.startsWith(packageName)) {
                            return inputR.substring(packageName.length(),packageName.length() + 1).equals(".");
                        }
                        return false;
                    }
                }));
        tmpClasses.addAll(ReflectionUtils.forNames(
                reflections.getStore().get(TypesScanner.class.getSimpleName()).keySet(),
                new ClassLoader[] { ClasspathHandler.class.getClassLoader() }));
    }
}
