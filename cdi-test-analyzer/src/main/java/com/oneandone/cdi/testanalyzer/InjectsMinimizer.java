package com.oneandone.cdi.testanalyzer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author aschoerk
 */
public class InjectsMinimizer {

    /**
     * Given
     * 
     * @param injects
     * @return
     */
    public static Set<QualifiedType> minimize(Collection<QualifiedType> injects, LeveledBuilder builder) {
        Set<QualifiedType> currentMinimum = new HashSet<>();
        for (QualifiedType q : injects) {
            Set<QualifiedType> altProducer = builder.alternativeMap.get(q.getRawtype());
            if (altProducer != null) {
                for (QualifiedType t : altProducer) {
                    if (t.isAssignableTo(q) && t.isAlternative())
                        q.setAlternative();
                }
            }
        }
        for (QualifiedType q : injects) {
            if (q.isProviderOrInstance())
                continue;
            boolean addIt = true;
            for (QualifiedType m : currentMinimum) {
                if (q.isAssignableTo(m) && q.isAlternative() == m.isAlternative()) {
                    addIt = false;
                    break;
                }
                if (m.isAssignableTo(q) && q.isAlternative() == m.isAlternative()) {
                    currentMinimum.remove(m);
                    break;
                }
            }
            if (addIt)
                currentMinimum.add(q);
        }
        return currentMinimum;
    }
}
