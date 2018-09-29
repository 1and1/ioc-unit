package com.oneandone.ejbcdiunit.cfganalyzer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aschoerk
 */
class ClasspathSetPopulator {
    Logger log = LoggerFactory.getLogger("ClasspathSetPopulator");

    private Class<?> loadClass(String name) {
        try {
            return getClass().getClassLoader().loadClass(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isDirectory(URL classpathEntry) {
        try {
            return new File(classpathEntry.toURI()).isDirectory();
        } catch (IllegalArgumentException ignore) {
            // Ignore, thrown by File constructor for unsupported URIs
        } catch (URISyntaxException ignore) {
            // Ignore, does not denote an URI that points to a directory
        }
        return false;
    }

    private URL getClasspathURL(Class<?> clazz) {
        CodeSource codeSource = clazz.getProtectionDomain()
                .getCodeSource();
        return codeSource != null ? codeSource.getLocation() : null;
    }

    private static Set<URL> getEntriesFromManifestClasspath(URL url)
            throws IOException {
        Set<URL> manifestURLs = new HashSet<>();
        // If this is a surefire manifest-only jar we need to get the original classpath.
        // When testing cdi-unit-tests through Maven, this finds extra entries compared to FCS:
        // eg ".../cdi-unit/cdi-unit-tests/target/classes"
        try (InputStream in = url.openStream();
                JarInputStream jar = new JarInputStream(in)) {
            Manifest manifest = jar.getManifest();
            if (manifest != null) {
                String classpath = (String) manifest.getMainAttributes()
                        .get(Attributes.Name.CLASS_PATH);
                if (classpath != null) {
                    String[] manifestEntries = classpath.split(" ?file:");
                    for (String entry : manifestEntries) {
                        if (entry.length() > 0) {
                            // entries is a Set, so this won't add duplicates
                            manifestURLs.add(new URL("file:" + entry));
                        }
                    }
                }
            }
        }
        return manifestURLs;
    }

    public void invoke(Set<URL> cdiClasspathEntries) throws IOException {

        String systemClasspath = System.getProperty("java.class.path");
        String pathseparator = System.getProperty("path.separator");
        String[] classpathes = systemClasspath.split(pathseparator);
        Set<URL> classpathEntries = new HashSet<>();
        for (String path: classpathes) {
            classpathEntries.add(new File(path).toURL());
        }

        // ClassLoader classLoader = ClasspathSetPopulator.class.getClassLoader();
        // Set<URL> classpathEntries = new HashSet<URL>(Arrays.asList(((URLClassLoader) classLoader).getURLs()));

        // If this is surefire we need to get the original claspath
        try (JarInputStream firstEntry = new JarInputStream(classpathEntries.iterator().next().openStream())) {
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
                // TODO beans.xml is no longer required by CDI (1.1+)
                URL resource = cl.getResource("META-INF/beans.xml");
                boolean ejbCdiUnit = url.equals(ClasspathSetPopulator.class.getProtectionDomain().getCodeSource().getLocation());
                if (ejbCdiUnit || resource != null || isDirectory(url)) {
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
    }

}
