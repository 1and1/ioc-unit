package net.oneandone.iocunit.data;

import java.util.Objects;

/**
 * @author aschoerk
 */
public class JarEntryInfo {
    private String fileType;
    private String name;
    private byte[] content;

    public JarEntryInfo(final String name, final String fileType, final byte[] classBytes) {
        this.content = classBytes;
        this.fileType = fileType;
        this.name = name;
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        JarEntryInfo entryInfo = (JarEntryInfo) o;
        return getName().equals(entryInfo.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    public String getFileType() {
        return fileType;
    }

    public String getName() {
        return name;
    }

    public byte[] getContent() {
        return content;
    }
}
