package net.oneandone.iocunit;

import static org.objectweb.asm.Opcodes.ASM4;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

import net.oneandone.iocunit.data.ClassInfo;
import net.oneandone.iocunit.data.JarEntryInfo;
import net.oneandone.iocunit.data.JarInfo;
import net.oneandone.iocunit.data.WarInfo;

/**
 * @author aschoerk
 */
public class ArchiveReader {
    static byte[] read(InputStream zis, long size) throws IOException {
        byte[] b = new byte[(int) size];
        int rb = 0;
        int chunk = 0;
        while (((int) size - rb) > 0) {
            chunk = zis.read(b, rb, (int) size - rb);
            if(chunk == -1) {
                if (rb < size) {
                    byte[] res = new byte[rb];
                    System.arraycopy(b,0,res,0,rb);
                    return res;
                }
                break;
            }
            rb += chunk;
        }
        return b;
    }


    static <i> ArrayList<JarEntryInfo> getEntryInfosFromZipFile(final File f) throws IOException {
        ArrayList<JarEntryInfo> entryInfos = new ArrayList<>();
        try (ZipFile zipFile = new ZipFile(f)) {
            Enumeration<?> enu = zipFile.entries();
            while (enu.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) enu.nextElement();
                String name = zipEntry.getName();
                long size = zipEntry.getSize();
                long compressedSize = zipEntry.getCompressedSize();
                if(zipEntry.isDirectory()) {
                    System.out.println("dir found:" + name);
                    Main.findings += ", " + name;
                    continue;
                }
                try (InputStream z = zipFile.getInputStream(zipEntry)) {
                    String fileType = name.substring(
                            name.lastIndexOf(".") + 1, name.length());
                    System.out.println("File type:" + fileType);
                    System.out.println("zipEntry: " + zipEntry);
                    byte[] classBytes = read(z, size);
                    entryInfos.add(new JarEntryInfo(name, fileType, classBytes));
                }
            }
        }
        return entryInfos;
    }

    static void addClass(final WarInfo warInfo, final JarInfo jarInfo, byte[] content) throws IOException {
        ClassReader cr = new ClassReader(new ByteArrayInputStream(content));
        System.out.println("Read class: " + cr.getClassName());
        final ClassInfo classData = new ClassInfo(cr.getClassName(), content);
        ClassVisitor classVisitor = new ClassVisitor(ASM4) {
            @Override
            public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
                classData.addAnnotation(descriptor);
                return super.visitAnnotation(descriptor, visible);
            }
        };
        cr.accept(classVisitor, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        jarInfo.addClassInfo(classData);
        warInfo.addClassInfo(classData);
    }

    static void readJarInfo(final WarInfo warInfo, final JarInfo jarInfo, final InputStream zippedInput) {
        try (ZipInputStream zis = new ZipInputStream(zippedInput)) {
            while (zis.available() != 0) {
                ZipEntry zipEntry = zis.getNextEntry();

                if(zipEntry != null) {
                    String name = zipEntry.getName();
                    long size = zipEntry.getSize();
                    long compressedSize = zipEntry.getCompressedSize();

                    System.out.printf("name: %-20s | size: %6d | compressed size: %6d\n",
                            name, size, compressedSize);

                    // directory ?
                    if(zipEntry.isDirectory()) {
                        System.out.println("dir found:" + name);
                        Main.findings += ", " + name;
                        continue;
                    }

                    if (size < 0) {
                        System.out.printf("name: %-20s | size: %6d | compressed size: %6d unreadable because of negative size\n",
                                name, size, compressedSize);
                        continue;
                    }
                    byte[] classBytes = read(zis, size);
                    if(name.toUpperCase().endsWith(".JAR") || name.toUpperCase().endsWith(".CLASS")) {
                        String fileType = name.substring(
                                name.lastIndexOf(".") + 1, name.length());
                        System.out.println("File type:" + fileType);
                        System.out.println("zipEntry: " + zipEntry);

                        if(fileType.equalsIgnoreCase("class")) {
                            addClass(warInfo, jarInfo, classBytes);
                        }
                        else if(fileType.equalsIgnoreCase("jar")) {
                            throw new RuntimeException("not expected jar in jar");
                        }
                    }
                    else {
                        System.out.println(name);
                        jarInfo.addResourceData(name, classBytes);
                    }
                }
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
