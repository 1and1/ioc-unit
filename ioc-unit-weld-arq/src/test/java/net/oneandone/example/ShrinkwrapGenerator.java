package net.oneandone.example;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Set;

import javax.enterprise.inject.spi.Extension;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.asset.UrlAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.weld.bootstrap.spi.Metadata;
import org.jboss.weld.injection.spi.EjbInjectionServices;

import com.oneandone.ejbcdiunit.cfganalyzer.TestConfigAnalyzer;

/**
 * @author aschoerk
 */
public class ShrinkwrapGenerator {

    private final TestConfigAnalyzer analyzer;
    private final Class<?> testClass;

    public ShrinkwrapGenerator(Class<?> testClass) {
        this.analyzer = new ShrinkwrapAnalyzer();
        this.testClass = testClass;
        try {
            analyzer.analyze(testClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    String createBeansXml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<beans xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://java.sun.com/xml/ns/javaee\"\n" +
                "       xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/beans_1_0.xsd\">\n");
        if (analyzer.getAlternatives().size() > 0) {
            sb.append("<alternatives>\n");
            for (String c : analyzer.getAlternatives()) {
                sb.append("<class>")
                        .append(c)
                        .append("</class>\n");
            }
            sb.append("</alternatives>\n");
        }
        if (analyzer.getEnabledAlternativeStereotypes().size() > 0) {
            sb.append("<stereotypes>\n");
            for (Metadata<String> c : analyzer.getEnabledAlternativeStereotypes()) {
                sb.append("<class>")
                        .append(c.getValue())
                        .append("</class>\n");
            }
            sb.append("</stereotypes>\n");
        }
        if (analyzer.getEnabledInterceptors().size() > 0) {
            sb.append("<interceptors>\n");
            for (Metadata<String> c : analyzer.getEnabledInterceptors()) {
                sb.append("<class>")
                        .append(c.getValue())
                        .append("</class>\n");
            }
            sb.append("</interceptors>\n");
        }

        sb.append("</beans>\n");
        return sb.toString();
    }

    JavaArchive create() throws ClassNotFoundException, UnsupportedEncodingException {
        JavaArchive result = ShrinkWrap.create(JavaArchive.class);
        final Set<String> discoveredClasses = analyzer.getDiscoveredClasses();

        discoveredClasses.remove(testClass.getName());
        for (String c : discoveredClasses) {
            result.addClass(Class.forName(c));
        }

        result.addClass(DeltaspikeClassDeactivator.class);
        Class<?>[] extensionClasses = new Class[analyzer.getExtensions().size()];
        int i = 0;
        for (Metadata<Extension> c : analyzer.getExtensions()) {
            extensionClasses[i++] = c.getValue().getClass();
        }
        result.addAsServiceProvider(Extension.class, extensionClasses);
        result.addAsServiceProvider(EjbInjectionServices.class, EjbInjectionService.class);


        for (URL u : analyzer.getClasspathEntries()) {
            String filename = u.getFile();
            if (filename.endsWith(".jar")) {
                if (!filename.contains("/jre/") && !filename.contains("/container-se")) {
                    int lastslash = filename.lastIndexOf("/");
                    String name = lastslash >= 0 ? filename.substring(lastslash + 1) : filename;
                    JavaArchive tmp = ShrinkWrap.createFromZipFile(JavaArchive.class, new File(filename));
                    result.merge(tmp);
                    // result.addAsLibrary(new UrlAsset(u), new BasicPath(name));
                }
            } else {
                result.add(new UrlAsset(u), "classes");
            }
        }


        System.setProperty("org.apache.deltaspike.core.spi.activation.ClassDeactivator", "net.oneandone.example.DeltaspikeClassDeactivator");

        result.addAsManifestResource(new ByteArrayAsset(createBeansXml().getBytes("UTF-8")), "beans.xml");
        System.out.println(result.toString());
        return result;
    }
}
