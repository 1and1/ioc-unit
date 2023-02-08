package net.oneandone.iocunit;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import net.oneandone.iocunit.data.JarEntryInfo;
import net.oneandone.iocunit.data.JarInfo;
import net.oneandone.iocunit.data.WarInfo;

/**
 * @author aschoerk
 */
public class WarAnalyzer {
    WarInfo warInfo = null;

    public WarInfo analyzeWarFile(final File f) throws IOException {
        warInfo = new WarInfo();
        ArrayList<JarEntryInfo> entryInfos = ArchiveReader.getEntryInfosFromZipFile(f);
        JarInfo warJarInfo = new JarInfo(f.getName());
        warInfo.setWar(warJarInfo);
        warInfo.addClassInfo(warJarInfo);
        warInfo.getJarData().put(warJarInfo.getName(), warJarInfo);
        for (JarEntryInfo e : entryInfos) {
            if(e.getFileType().equalsIgnoreCase("class")) {
                ArchiveReader.addClass(warInfo, warJarInfo, e.getContent());
            }
            else if(e.getFileType().equalsIgnoreCase("jar")) {
                System.out.println("Opening Jar as stream: " + e.getName());
                Main.findings += ", " + e.getName();
                exctractInfoFromZip(e);
            }
            else {
                warJarInfo.addResourceData(e.getName(), e.getContent());
            }
        }
        return warInfo;
    }

    //The idea was recursively extract entries using ZipFile
    private void exctractInfoFromZip(JarEntryInfo e) throws IOException {
        JarInfo jarInfo = new JarInfo(e.getName());
        warInfo.addClassInfo(jarInfo);
        try(final ByteArrayInputStream zippedInput = new ByteArrayInputStream(e.getContent())) {
            ArchiveReader.readJarInfo(warInfo, jarInfo, zippedInput);
        }
    }

}
