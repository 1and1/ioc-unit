package net.oneandone.iocunit.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author aschoerk
 */
public class ClassInfo {
    String name;
    private byte[] content;
    private List<String> annotations;

    public ClassInfo(final String name, final byte[] content) {
        this.name = name;
        this.content = content;
    }

    public void addAnnotation(String annotation) {
        if(annotations == null) {
            annotations = new ArrayList<>();
        }
        annotations.add(annotation);
    }


    public List<String> getAnnotations() {
        if(annotations != null) {
            return annotations;
        }
        else {
            return Collections.emptyList();
        }
    }

    public byte[] getContent() {
        return content;
    }
    public String getName() {
        return name;
    }
}
