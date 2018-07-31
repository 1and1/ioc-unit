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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.jglue.cdiunit.CdiRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.ejbcdiunit.EjbUnitRunner;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;

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

    public Set<URL> invoke(Set<URL> cdiClasspathEntries) throws IOException {
        List<URL> entryList = new FastClasspathScanner().scan()
                .getUniqueClasspathElementURLs();
        // cdiClasspathEntries doesn't preserve order, so HashSet is fine
        Set<URL> entrySet = new HashSet<>(entryList);

        for (URL url : entryList) {
            entrySet.addAll(getEntriesFromManifestClasspath(url));
        }

        for (URL url : entrySet) {
            try (URLClassLoader classLoader = new URLClassLoader(new URL[] { url },
                    null)) {
                // TODO this seems pretty Maven-specific, and fragile
                if (url.getFile().endsWith("/classes/")) {
                    URL webInfBeans = new URL(url,
                            "../../src/main/webapp/WEB-INF/beans.xml");
                    try (InputStream ignore = webInfBeans.openStream()) {
                        cdiClasspathEntries.add(url);
                    } catch (IOException ignore) {
                        // no such file
                    }
                }
                // TODO beans.xml is no longer required by CDI (1.1+)
                URL beansXml = classLoader.getResource("META-INF/beans.xml");
                boolean isCdiUnit = url.equals(getClasspathURL(CdiRunner.class));
                if (isCdiUnit || beansXml != null || isDirectory(url)) {
                    cdiClasspathEntries.add(url);
                }
            }
        }
        log.debug("CDI classpath entries discovered:");
        for (URL url : cdiClasspathEntries) {
            log.debug("{}", url);
        }
        return entrySet;
    }


    public ArrayList<URL> invokeOld(Set<URL> cdiClasspathEntries) throws IOException {

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
                boolean cdiUnit = url.equals(CdiRunner.class.getProtectionDomain().getCodeSource().getLocation());
                if (ejbCdiUnit || cdiUnit || resource != null || isDirectory(url)) {
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

}
