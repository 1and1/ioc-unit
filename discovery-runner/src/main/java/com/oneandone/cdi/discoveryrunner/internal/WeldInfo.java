package com.oneandone.cdi.discoveryrunner.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author aschoerk
 */
public class WeldInfo {
    private Set<String> toExcludeClassNames = new HashSet<>();
    private List<String> toExcludeClassNameParts = new ArrayList<>();
    private Set<String> toExcludeExpressions = new HashSet<>();
    private Set<Class<?>> alternatives = new HashSet<>();
    private Set<Class<?>> toAdd = new HashSet<>();

    public Set<String> getToExcludeClassNames() {
        return toExcludeClassNames;
    }

    public List<String> getToExcludeClassNameParts() {
        return toExcludeClassNameParts;
    }

    public Set<String> getToExcludeExpressions() {
        return toExcludeExpressions;
    }

    public Set<Class<?>> getAlternatives() {
        return alternatives;
    }

    public Set<Class<?>> getToAdd() {
        return toAdd;
    }
}
