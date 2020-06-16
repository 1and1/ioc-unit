package net.oneandone.iocunit;

import java.net.URL;

import net.oneandone.iocunit.data.ClassInfo;
import net.oneandone.iocunit.data.WarInfo;

/**
 * @author aschoerk
 */
class WarClassLoader extends ClassLoader {
    private final WarInfo warInfo;

    public WarClassLoader(WarInfo warInfo) {
        super(null);
        this.warInfo = warInfo;
    }

    @Override
    protected URL findResource(final String name) {
        if (name.equals("META-INF/beans.xml") || name.equals("WEB-INF/beans.xml")) {

            return null;
        } else {
            return super.findResource(name);
        }
    }

    @Override
    public URL getResource(final String name) {
        return super.getResource(name);
    }

    @Override
    public Class findClass(String name) throws ClassNotFoundException {
        final String className = name.replace(".", "/");
        final ClassInfo classInfo = warInfo.getFlatClassData().get(className);
        if(classInfo != null) {
            System.out.println("WarClassLoader found " + name);
            byte[] data = classInfo.getContent();
            return defineClass(name, data, 0, data.length);
        }
        else {
            System.out.println("WarClassLoader notfd " + name);
            return super.findClass(name);
        }

    }
}
