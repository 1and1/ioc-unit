package net.oneandone.iocunit.data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author aschoerk
 */
public class WarInfo {
    private Map<String, ClassInfo> flatClassData = new HashMap<>();
    private Map<String, JarInfo> jarData = new HashMap<>();
    private JarInfo warJarInfo;

    public void addClassInfo(final ClassInfo classData) {
        getFlatClassData().put(classData.name, classData);
    }
    public void addClassInfo(final JarInfo jarInfo) {
        getJarData().put(jarInfo.getName(), jarInfo);
    }

    public void setWar(final JarInfo warJarInfoP) {
        this.warJarInfo = warJarInfoP;
    }

    public Map<String, ClassInfo> getFlatClassData() {
        return flatClassData;
    }

    public Map<String, JarInfo> getJarData() {
        return jarData;
    }
}
