package com.oneandone.cdi.discoveryrunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author aschoerk
 */
public class WeldInfo {
    Set<String> toExcludeClassNames = new HashSet<>();
    List<String> toExcludeClassNameParts = new ArrayList<>();
    Set<String> toExcludeExpressions = new HashSet<>();
    Set<Class<?>> alternatives = new HashSet<>();
    Set<Class<?>> toAdd = new HashSet<>();
}
