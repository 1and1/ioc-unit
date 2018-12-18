package com.oneandone.cdi.testanalyzer;

import java.util.Collection;
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
    public static HashMultiMap<QualifiedType, QualifiedType> minimize(Collection<QualifiedType> injects, LeveledBuilder builder) {
        HashMultiMap<QualifiedType, QualifiedType> currentMinimum = new HashMultiMap<>();
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
            for (QualifiedType m : currentMinimum.keySet()) {
                if (q.isAssignableTo(m) && q.isAlternative() == m.isAlternative()) {
                    addIt = false;
                    currentMinimum.put(m, q);
                    break;
                }
                if (m.isAssignableTo(q) && q.isAlternative() == m.isAlternative()) {
                    Set<QualifiedType> tmp = currentMinimum.remove(m);
                    tmp.add(q);
                    currentMinimum.put(q, tmp);
                    addIt = false;
                    break;
                }
            }
            if (addIt)
                currentMinimum.put(q, q);
        }
        return currentMinimum;
    }
}
