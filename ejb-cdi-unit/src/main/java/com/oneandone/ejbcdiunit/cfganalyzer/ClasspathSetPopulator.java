package com.oneandone.ejbcdiunit.cfganalyzer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.ejbcdiunit.EjbUnitRunner;

/**
 * @author aschoerk
 */
class ClasspathSetPopulator {
    Logger log = LoggerFactory.getLogger("ClasspathSetPopulator");

    public ArrayList<URL> invoke(Set<URL> cdiClasspathEntries) throws IOException {
        ClassLoader classLoader = ClasspathSetPopulator.class.getClassLoader();
        ArrayList<URL> classpathEntries = new ArrayList<URL>(Arrays.asList(((URLClassLoader) classLoader).getURLs()));

        // If this is surefire we need to get the original claspath
        try (JarInputStream firstEntry = new JarInputStream(classpathEntries.get(0).openStream())) {
            Manifest manifest = firstEntry.getManifest();
            if (manifest != null) {
                String classpath = (String) manifest.getMainAttributes().get(Attributes.Name.CLASS_PATH);
                if (classpath != null) {
                    String[] manifestEntries = classpath.split(" ?file:");
                    for (String entry : manifestEntries) {
                        if (!entry.isEmpty()) {
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
                    URL webInfBeans = new URL(url, "../../src/main/WEB-INF/beans.xml");
                    try {
                        webInfBeans.openConnection().connect();
                        cdiClasspathEntries.add(url);
                    } catch (IOException e) {

                    }
                }
                URL resource = cl.getResource("META-INF/beans.xml");
                boolean ejbCdiUnit = url.equals(EjbUnitRunner.class.getProtectionDomain().getCodeSource().getLocation());
                if (ejbCdiUnit || resource != null || isDirectoryOnClasspath(url)) {
                    cdiClasspathEntries.add(url);
                }
            } finally {
                try {
                    Method method = cl.getClass().getMethod("close");
                    method.invoke(cl);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    // Ignore, we might be running on Java 6
                }
            }
        }
        log.trace("CDI classpath classpathEntries discovered:");
        for (URL url : cdiClasspathEntries) {
            log.trace("{}", url);
        }
        return classpathEntries;
    }

    private boolean isDirectoryOnClasspath(URL classpathEntry) {
        try {
            return new File(classpathEntry.toURI()).isDirectory();
        } catch (IllegalArgumentException | URISyntaxException e) {
            // Ignore, thrown by File constructor for unsupported URIs
        }
        return false;
    }
}
