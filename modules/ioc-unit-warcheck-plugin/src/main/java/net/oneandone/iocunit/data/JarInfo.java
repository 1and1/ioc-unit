package net.oneandone.iocunit.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author aschoerk
 */
public class JarInfo {
    Map<String, ClassInfo> jarClassData = new HashMap<>();
    boolean hasBeansXml;
    Map<String, byte[]> jarResourceData = new HashMap<>();
    Map<String, List<ClassInfo>> annotationReferences = new HashMap<>();
    private String name;
    private boolean hasEjbJar;

    public JarInfo(final String name) {
        this.name = name;
    }

    public Map<String, ClassInfo> getJarClassData() {
        return jarClassData;
    }

    public boolean isHasBeansXml() {
        return hasBeansXml;
    }

    public Map<String, byte[]> getJarResourceData() {
        return jarResourceData;
    }

    public Map<String, List<ClassInfo>> getAnnotationReferences() {
        return annotationReferences;
    }

    public boolean isHasEjbJar() {
        return hasEjbJar;
    }

    public void addClassInfo(ClassInfo classInfo) {
        jarClassData.put(classInfo.name, classInfo);
        for (String ann: classInfo.getAnnotations()) {
            List<ClassInfo> annotatedClasses = annotationReferences.get(ann);
            if (annotatedClasses == null) {
                annotatedClasses = new ArrayList<>();
                annotationReferences.put(ann, annotatedClasses);
            }
            annotatedClasses.add(classInfo);
        }
    }

    public void addResourceData(String resourceName, byte[] data) {
        jarResourceData.put(resourceName, data);
        if (resourceName.equals("WEB-INF/beans.xml") || resourceName.equals("META-INF/beans.xml"))
            hasBeansXml = true;
        if (resourceName.equals("WEB-INF/ejb.jar.xml") || resourceName.equals("META-INF/ejb-jar.xml"))
            hasEjbJar = true;

    }

    public String getName() {
        return name;
    }
}
