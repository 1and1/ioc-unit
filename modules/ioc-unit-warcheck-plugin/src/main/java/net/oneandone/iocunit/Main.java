package net.oneandone.iocunit;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.xeustechnologies.jcl.JarClassLoader;

import com.jcabi.aether.Aether;

import net.oneandone.iocunit.data.ClassInfo;
import net.oneandone.iocunit.data.JarEntryInfo;
import net.oneandone.iocunit.data.JarInfo;
import net.oneandone.iocunit.data.WarInfo;

/**
 * @author aschoerk
 */
public class Main {
    static String findings = new String();
    static JarClassLoader jarClassLoader = new JarClassLoader();
    static String[][] artifacts = {
            {"org.jboss.spec.javax.annotation", "jboss-annotations-api_1.2_spec", "1.0.0.Final"},
            {"org.jboss.spec.javax.ejb", "jboss-ejb-api_3.2_spec", "1.0.0.Final"},
            {"org.jboss.spec.javax.el", "jboss-el-api_3.0_spec", "1.0.7.Final"},
            {"javax.enterprise", "cdi-api", "1.2"},
            {"javax.inject", "javax.inject", "1"},
            {"org.jboss.spec.javax.interceptor", "jboss-interceptors-api_1.2_spec", "1.0.0.Final"},
            {"org.jboss.spec.javax.jms", "jboss-jms-api_2.0_spec", "1.0.0.Final"},
            {"org.hibernate.javax.persistence", "hibernate-jpa-2.1-api", "1.0.0.Final"},
            {"org.jboss.spec.javax.servlet", "jboss-servlet-api_3.1_spec", "1.0.0.Final"},
            {"org.jboss.spec.javax.transaction", "jboss-transaction-api_1.2_spec", "1.0.0.Final"},
            {"org.jboss.spec.javax.ws.rs", "jboss-jaxrs-api_2.0_spec", "1.0.0.Final"},
            {"org.jboss.spec.javax.xml.bind", "jboss-jaxb-api_2.2_spec", "1.0.4.Final"},
            {"org.jboss.spec.javax.xml.rpc", "jboss-jaxrpc-api_1.1_spec", "1.0.1.Final"},
            {"org.jboss.spec.javax.xml.soap", "jboss-saaj-api_1.3_spec", "1.0.3.Final"},
            {"org.jboss.spec.javax.xml.ws", "jboss-jaxws-api_2.2_spec", "2.0.3.Final"},
            {"org.jboss.weld.se","weld-se","2.3.5.Final"},
            {"org.jboss.weld","weld-spi","2.3.Final"}
    };

    public static void main(String[] args) {
        File f = new File("/home/aschoerk/projects/github/ioc-unit-main/testing/ioc-unit-test-war/target/ioc-unit-test-war-2.0.51-SNAPSHOT.war");
        try {
            WarInfo warInfo = new WarAnalyzer().analyzeWarFile(f);
            final Class<WeldStarter> weldStarterClass = WeldStarter.class;
            final String classPath = weldStarterClass.getName().replace(".", "/");
            URL resource = weldStarterClass.getResource("/" + classPath + ".class");
            try(InputStream is = resource.openStream()) {
                byte[] content = ArchiveReader.read(is, 100000);
                ClassInfo classInfo = new ClassInfo(classPath, content);
                warInfo.addClassInfo(classInfo);
            }

            File local = new File("/tmp/local-repository");
            Collection<RemoteRepository> remotes = Arrays.asList(
                    new RemoteRepository(
                            "maven-central",
                            "default",
                            "https://repo1.maven.org/maven2/"
                    )
            );

            final Aether aether = new Aether(remotes, local);
            List<Collection<Artifact>> artifactDeps = new ArrayList<>();
            for (String[] a : artifacts) {
                final List<Artifact> resolved = aether.resolve(
                        new DefaultArtifact(a[0], a[1], "", "jar", a[2]), "runtime");
                for (Artifact artifact : resolved) {

                    final File file = artifact.getFile();
                    ArrayList<JarEntryInfo> entries = ArchiveReader.getEntryInfosFromZipFile(file);
                    JarInfo jarInfo = new JarInfo(file.getName());
                    warInfo.addClassInfo(jarInfo);
                    for (JarEntryInfo e : entries) {
                        if(e.getFileType().equalsIgnoreCase("class")) {
                            ArchiveReader.addClass(warInfo, jarInfo, e.getContent());
                        }
                        else if(e.getFileType().equalsIgnoreCase("jar")) {
                            throw new RuntimeException("jar in jar is not supported");
                        }
                        else {
                            jarInfo.addResourceData(e.getName(), e.getContent());
                        }
                    }
                }
                artifactDeps.add(resolved);
            }
            ClassLoader cl = new WarClassLoader(warInfo);
            Class appScopedServiceBeanClass = cl.loadClass("com.oneandone.iocunitejb.cdibeans.AppScopedServiceBean");
            Class indirectClass = cl.loadClass("org.junit.rules.DisableOnDebug");
            // Class mainClass = cl.loadClass("net.oneandone.iocunit.Main");
            Class jmsClass = cl.loadClass("javax.jms.JMSConnectionFactory");

            // assert (Main.class.equals(mainClass));


            List<List<Class<?>>> jarClassesToStart = warInfo.getJarData().values()
                    .stream().filter(JarInfo::isHasBeansXml)
                    .map(
                            jd -> jd.getAnnotationReferences()
                                    .entrySet()
                                    .stream()
                                    .filter(e -> isEjbOrCDI(e.getKey()))
                                    .map(e -> e.getValue())
                                    .flatMap(Collection::stream)
                                    .map(n -> {
                                        try {
                                            return cl.loadClass(n.getName().replace("/", "."));
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                            return Object.class;
                                        }
                                    })
                                    .collect(Collectors.toList())
                    ).collect(Collectors.toList());

            Class starter = (Class)cl.loadClass(weldStarterClass.getName());
            Object instance = starter.getConstructor().newInstance();

            for (Method m: starter.getMethods()) {
                if (m.getName().equals("start")) {
                    m.invoke(instance, jarClassesToStart);
                }
            }

        } catch (
                Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static boolean isEjbOrCDI(final String key) {
        int pos = key.lastIndexOf("/");
        switch (key.substring(pos + 1)) {
            case "Stateless;":
            case "Stateful;":
            case "ApplicationScoped;":
            case "SessionScoped;":
            case "ThreadScoped;":
            case "BeansScoped":
            case "Singleton;":
            case "MessageDriven;":
                return true;
            default:
                return false;
        }
    }


}
