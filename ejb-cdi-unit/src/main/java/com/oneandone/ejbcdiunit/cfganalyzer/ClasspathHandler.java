package com.oneandone.ejbcdiunit.cfganalyzer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.function.Predicate;

import org.reflections8.ReflectionUtils;
import org.reflections8.Reflections;
import org.reflections8.util.ConfigurationBuilder;

import com.oneandone.ejbcdiunit.internal.TypesScanner;

/**
 * @author aschoerk
 */
public class ClasspathHandler {

    public static void addPackage(Class<?> additionalPackage, Set<Class<?>> classesToProcess) throws MalformedURLException {
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
                new ClassLoader[] { ClasspathHandler.class.getClassLoader() }));
    }

    public static void addClassPath(Class<?> additionalClasspath, Set<Class<?>> classesToProcess) throws MalformedURLException {
        addClassPath(additionalClasspath, classesToProcess, null);
    }


    public static void addClassPath(Class<?> additionalClasspath, Set<Class<?>> classesToProcess, Set<URL> classpathEntries)
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

}
